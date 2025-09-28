package org.garlikoff.restdata.service;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.FlushParam;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.MetricType;
import io.milvus.grpc.FlushResponse;
import io.milvus.response.SearchResultsWrapper;
import org.garlikoff.restdata.config.MilvusProperties;
import org.garlikoff.restdata.model.RealEstateObject;
import org.garlikoff.restdata.model.RealEstateObjectParam;
import org.garlikoff.restdata.model.TranslationId;
import org.garlikoff.restdata.repo.RealEstateObjectParamRepository;
import org.garlikoff.restdata.repo.RealEstateObjectRepository;
import org.garlikoff.restdata.repo.TranslationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Сервис синхронизации объектов недвижимости с Milvus и выполнения поиска.
 */
@Service
public class RealEstateMilvusService {
    private static final String FIELD_OBJECT_ID = "object_id";
    private static final String FIELD_PARAM_ID = "param_id";
    private static final String FIELD_USER_ID = "user_id";
    private static final String FIELD_DESCRIPTION = "description";

    private final MilvusServiceClient client;
    private final MilvusProperties properties;
    private final RealEstateObjectRepository objectRepository;
    private final RealEstateObjectParamRepository paramRepository;
    private final TranslationRepository translationRepository;
    private final SimpleEmbeddingService embeddingService;
    private final RealEstateDescriptionBuilder descriptionBuilder;

    public RealEstateMilvusService(MilvusServiceClient client,
                                   MilvusProperties properties,
                                   RealEstateObjectRepository objectRepository,
                                   RealEstateObjectParamRepository paramRepository,
                                   TranslationRepository translationRepository,
                                   SimpleEmbeddingService embeddingService,
                                   RealEstateDescriptionBuilder descriptionBuilder) {
        this.client = client;
        this.properties = properties;
        this.objectRepository = objectRepository;
        this.paramRepository = paramRepository;
        this.translationRepository = translationRepository;
        this.embeddingService = embeddingService;
        this.descriptionBuilder = descriptionBuilder;
    }

    /**
     * Выполняет полную синхронизацию всех объектов недвижимости в Milvus.
     *
     * @return количество загруженных записей
     */
    @Transactional(readOnly = true)
    public synchronized int synchronizeAll() {
        recreateCollection();
        List<RealEstateObject> objects = streamOf(objectRepository.findAll())
            .sorted(Comparator.comparing(RealEstateObject::getId))
            .toList();
        List<RealEstateObjectParam> params = streamOf(paramRepository.findAll())
            .sorted(Comparator.comparing(RealEstateObjectParam::getId))
            .toList();
        Map<UUID, Deque<RealEstateObjectParam>> paramsByUser = params.stream()
            .filter(param -> param.getUser() != null)
            .collect(Collectors.groupingBy(param -> param.getUser().getId(), Collectors.toCollection(ArrayDeque::new)));
        if (objects.isEmpty()) {
            return 0;
        }
        Set<String> dictionaryKeys = collectDictionaryKeys(params);
        Map<String, String> translations = loadTranslations(dictionaryKeys);
        List<RealEstateVectorRecord> records = new ArrayList<>();
        for (RealEstateObject object : objects) {
            if (object.getUser() == null) {
                continue;
            }
            Deque<RealEstateObjectParam> queue = paramsByUser.get(object.getUser().getId());
            if (queue == null || queue.isEmpty()) {
                continue;
            }
            RealEstateObjectParam param = queue.pollFirst();
            if (param == null) {
                continue;
            }
            String description = descriptionBuilder.buildDescription(param, translations);
            if (description.length() > properties.getDescriptionMaxLength()) {
                description = description.substring(0, properties.getDescriptionMaxLength()).trim();
            }
            float[] embedding = embeddingService.embed(description, properties.getDimension());
            UUID userId = Optional.ofNullable(param.getUser())
                .map(user -> user.getId())
                .orElseGet(() -> Optional.ofNullable(object.getUser()).map(user -> user.getId()).orElse(null));
            records.add(new RealEstateVectorRecord(object.getId(), param.getId(), userId, description, embedding));
        }
        if (records.isEmpty()) {
            return 0;
        }
        insertRecords(records);
        return records.size();
    }

    /**
     * Выполняет поиск объектов по текстовому запросу.
     *
     * @param query текст запроса
     * @param limit максимальное число результатов
     * @return найденные объекты
     */
    public synchronized List<RealEstateSearchResult> search(String query, int limit) {
        ensureCollection();
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }
        float[] vector = embeddingService.embed(query, properties.getDimension());
        List<List<Float>> vectors = List.of(toList(vector));
        SearchParam.Builder searchBuilder = SearchParam.newBuilder();
        searchBuilder = invokeBuilder(searchBuilder, properties.getCollection(),
            "withCollectionName", "withCollection", "withCollectionNames");
        searchBuilder = invokeBuilder(searchBuilder, resolveMetricType(),
            "withMetricType");
        searchBuilder = invokeBuilder(searchBuilder,
            List.of(FIELD_OBJECT_ID, FIELD_PARAM_ID, FIELD_USER_ID, FIELD_DESCRIPTION),
            "withOutFields", "withOutputFields");
        searchBuilder = invokeBuilder(searchBuilder, Math.max(1, limit),
            "withTopK", "withTopk");
        searchBuilder = invokeBuilder(searchBuilder, vectors,
            "withVectors");
        searchBuilder = invokeBuilder(searchBuilder, properties.getVectorField(),
            "withVectorFieldName", "withVectorField");
        searchBuilder = invokeBuilder(searchBuilder, properties.getSearchParams(),
            "withParams", "withSearchParams");
        SearchParam searchParam = searchBuilder.build();
        io.milvus.param.R<io.milvus.grpc.SearchResults> response = client.search(searchParam);
        check(response, "search vectors");
        SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());
        return extractResults(wrapper);
    }

    private synchronized void ensureCollection() {
        HasCollectionParam.Builder hasBuilder = HasCollectionParam.newBuilder();
        hasBuilder = invokeBuilder(hasBuilder, properties.getCollection(),
            "withCollectionName", "withCollection", "withCollectionNames");
        HasCollectionParam hasCollectionParam = hasBuilder.build();
        R<Boolean> exists = client.hasCollection(hasCollectionParam);
        check(exists, "check collection existence");
        if (Boolean.TRUE.equals(exists.getData())) {
            return;
        }
        createCollection();
    }

    private synchronized void recreateCollection() {
        HasCollectionParam.Builder hasBuilder = HasCollectionParam.newBuilder();
        hasBuilder = invokeBuilder(hasBuilder, properties.getCollection(),
            "withCollectionName", "withCollection", "withCollectionNames");
        HasCollectionParam hasCollectionParam = hasBuilder.build();
        R<Boolean> exists = client.hasCollection(hasCollectionParam);
        check(exists, "check collection existence");
        if (Boolean.TRUE.equals(exists.getData())) {
            dropCollection();
        }
        createCollection();
    }

    private void dropCollection() {
        io.milvus.param.collection.DropCollectionParam.Builder dropBuilder = io.milvus.param.collection.DropCollectionParam.newBuilder();
        dropBuilder = invokeBuilder(dropBuilder, properties.getCollection(),
            "withCollectionName", "withCollection", "withCollectionNames");
        io.milvus.param.collection.DropCollectionParam dropCollectionParam = dropBuilder.build();
        R<?> dropResponse = client.dropCollection(dropCollectionParam);
        check(dropResponse, "drop collection");
    }

    private void createCollection() {
        FieldType primary = FieldType.newBuilder()
            .withName("id")
            .withDataType(DataType.Int64)
            .withPrimaryKey(true)
            .withAutoID(true)
            .build();
        FieldType objectId = FieldType.newBuilder()
            .withName(FIELD_OBJECT_ID)
            .withDataType(DataType.VarChar)
            .withMaxLength(64)
            .build();
        FieldType paramId = FieldType.newBuilder()
            .withName(FIELD_PARAM_ID)
            .withDataType(DataType.VarChar)
            .withMaxLength(64)
            .build();
        FieldType userId = FieldType.newBuilder()
            .withName(FIELD_USER_ID)
            .withDataType(DataType.VarChar)
            .withMaxLength(64)
            .build();
        FieldType description = FieldType.newBuilder()
            .withName(FIELD_DESCRIPTION)
            .withDataType(DataType.VarChar)
            .withMaxLength(properties.getDescriptionMaxLength())
            .build();
        FieldType vector = FieldType.newBuilder()
            .withName(properties.getVectorField())
            .withDataType(DataType.FloatVector)
            .withDimension(properties.getDimension())
            .build();
        CreateCollectionParam.Builder builder = CreateCollectionParam.newBuilder();
        builder = invokeBuilder(builder, properties.getCollection(),
            "withCollectionName", "withCollection", "withCollectionNames");
        builder.withDescription("Real estate objects embeddings")
            .withShardsNum(properties.getShardsNum())
            .addFieldType(primary)
            .addFieldType(objectId)
            .addFieldType(paramId)
            .addFieldType(userId)
            .addFieldType(description)
            .addFieldType(vector);
        CreateCollectionParam createCollectionParam = builder.build();
        R<?> createResponse = client.createCollection(createCollectionParam);
        check(createResponse, "create collection");
        CreateIndexParam.Builder indexBuilder = CreateIndexParam.newBuilder();
        indexBuilder = invokeBuilder(indexBuilder, properties.getCollection(),
            "withCollectionName", "withCollection", "withCollectionNames");
        indexBuilder.withFieldName(properties.getVectorField())
            .withIndexName(properties.getVectorField() + "_idx")
            .withMetricType(resolveMetricType());
        configureIndexBuilder(indexBuilder);
        CreateIndexParam indexParam = indexBuilder.build();
        R<?> indexResponse = client.createIndex(indexParam);
        check(indexResponse, "create index");
    }

    private void insertRecords(List<RealEstateVectorRecord> records) {
        List<String> objectIds = new ArrayList<>(records.size());
        List<String> paramIds = new ArrayList<>(records.size());
        List<String> userIds = new ArrayList<>(records.size());
        List<String> descriptions = new ArrayList<>(records.size());
        List<List<Float>> vectors = new ArrayList<>(records.size());
        for (RealEstateVectorRecord record : records) {
            objectIds.add(record.objectId().toString());
            paramIds.add(record.paramId().toString());
            userIds.add(record.userId() != null ? record.userId().toString() : "");
            descriptions.add(record.description());
            vectors.add(toList(record.vector()));
        }
        List<InsertParam.Field> fields = List.of(
            new InsertParam.Field(FIELD_OBJECT_ID, objectIds),
            new InsertParam.Field(FIELD_PARAM_ID, paramIds),
            new InsertParam.Field(FIELD_USER_ID, userIds),
            new InsertParam.Field(FIELD_DESCRIPTION, descriptions),
            new InsertParam.Field(properties.getVectorField(), vectors)
        );
        InsertParam.Builder insertBuilder = InsertParam.newBuilder();
        insertBuilder = invokeBuilder(insertBuilder, properties.getCollection(),
            "withCollectionName", "withCollection", "withCollectionNames");
        insertBuilder = invokeBuilder(insertBuilder, fields, "withFields");
        InsertParam insertParam = insertBuilder.build();
        R<io.milvus.grpc.MutationResult> insertResponse = client.insert(insertParam);
        check(insertResponse, "insert records");
        FlushParam.Builder flushBuilder = FlushParam.newBuilder();
        flushBuilder = invokeBuilder(flushBuilder, properties.getCollection(),
            "withCollectionName", "withCollection", "withCollectionNames");
        R<FlushResponse> flushResponse = client.flush(flushBuilder.build());
        check(flushResponse, "flush collection");
        LoadCollectionParam.Builder loadBuilder = LoadCollectionParam.newBuilder();
        loadBuilder = invokeBuilder(loadBuilder, properties.getCollection(),
            "withCollectionName", "withCollection", "withCollectionNames");
        R<?> loadResponse = client.loadCollection(loadBuilder.build());
        check(loadResponse, "load collection");
    }

    private Map<String, String> loadTranslations(Set<String> keys) {
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }
        List<TranslationId> ids = keys.stream()
            .map(key -> {
                TranslationId id = new TranslationId();
                id.setWordKey(key);
                id.setLanguageKey(properties.getLanguage());
                return id;
            })
            .toList();
        Map<String, String> translations = new HashMap<>();
        translationRepository.findAllById(ids).forEach(translation ->
            translations.put(translation.getId().getWordKey(), translation.getValue())
        );
        return translations;
    }

    private Set<String> collectDictionaryKeys(List<RealEstateObjectParam> params) {
        Set<String> keys = new HashSet<>();
        for (RealEstateObjectParam param : params) {
            if (param.getLocation() != null && param.getLocation().getNameKey() != null) {
                keys.add(param.getLocation().getNameKey());
            }
            collectWordKey(param.getType(), keys);
            collectWordKey(param.getFurnishings(), keys);
            collectWordKey(param.getBalconyTerrace(), keys);
            collectWordKey(param.getGarageParking(), keys);
            collectWordKey(param.getGardenYard(), keys);
            collectWordKey(param.getHousingCondition(), keys);
            collectWordKey(param.getFloor(), keys);
            collectWordKey(param.getHeating(), keys);
            collectWordKey(param.getEnergyCertificate(), keys);
            collectWordKey(param.getOrientation(), keys);
        }
        return keys;
    }

    private static void collectWordKey(Object word, Set<String> keys) {
        if (word == null) {
            return;
        }
        try {
            String key = (String) word.getClass().getMethod("getKey").invoke(word);
            if (key != null) {
                keys.add(key);
            }
        } catch (ReflectiveOperationException ignored) {
            // нет доступа к ключу слова
        }
    }

    private static List<Float> toList(float[] vector) {
        List<Float> list = new ArrayList<>(vector.length);
        for (float value : vector) {
            list.add(value);
        }
        return list;
    }

    private static UUID parseUuid(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString();
        if (text.isBlank()) {
            return null;
        }
        return UUID.fromString(text);
    }

    private List<RealEstateSearchResult> extractResults(SearchResultsWrapper wrapper) {
        List<RealEstateSearchResult> results = tryExtractRowRecords(wrapper);
        if (!results.isEmpty()) {
            return results;
        }
        return extractFromColumns(wrapper);
    }

    private List<RealEstateSearchResult> tryExtractRowRecords(SearchResultsWrapper wrapper) {
        try {
            Method getRowRecords = wrapper.getClass().getMethod("getRowRecords");
            Object rawRows = getRowRecords.invoke(wrapper);
            if (!(rawRows instanceof List<?> rows) || rows.isEmpty()) {
                return Collections.emptyList();
            }
            List<RealEstateSearchResult> results = new ArrayList<>(rows.size());
            for (Object row : rows) {
                Object objectIdValue = invokeRow(row, "getFieldValue", FIELD_OBJECT_ID);
                Object paramIdValue = invokeRow(row, "getFieldValue", FIELD_PARAM_ID);
                Object userIdValue = invokeRow(row, "getFieldValue", FIELD_USER_ID);
                Object descriptionValue = invokeRow(row, "getFieldValue", FIELD_DESCRIPTION);
                Object scoreValue = invokeRow(row, "getScore");
                float score = scoreValue instanceof Number number ? number.floatValue() : 0F;
                UUID objectId = parseUuid(objectIdValue);
                UUID paramId = parseUuid(paramIdValue);
                UUID userId = parseUuid(userIdValue);
                String description = descriptionValue != null ? descriptionValue.toString() : "";
                results.add(new RealEstateSearchResult(objectId, paramId, userId, score, description));
            }
            return results;
        } catch (ReflectiveOperationException ignored) {
            return Collections.emptyList();
        }
    }

    private List<RealEstateSearchResult> extractFromColumns(SearchResultsWrapper wrapper) {
        List<?> objectIds = getFieldData(wrapper, FIELD_OBJECT_ID);
        List<?> paramIds = getFieldData(wrapper, FIELD_PARAM_ID);
        List<?> userIds = getFieldData(wrapper, FIELD_USER_ID);
        List<?> descriptions = getFieldData(wrapper, FIELD_DESCRIPTION);
        List<Float> scores = getScores(wrapper);
        int rowCount = Math.max(Math.max(objectIds.size(), paramIds.size()), Math.max(userIds.size(), descriptions.size()));
        rowCount = Math.max(rowCount, scores.size());
        if (rowCount == 0) {
            int inferred = getRowCount(wrapper);
            rowCount = Math.max(rowCount, inferred);
        }
        if (rowCount == 0) {
            return Collections.emptyList();
        }
        List<RealEstateSearchResult> results = new ArrayList<>(rowCount);
        for (int i = 0; i < rowCount; i++) {
            UUID objectId = parseUuid(getValue(objectIds, i));
            UUID paramId = parseUuid(getValue(paramIds, i));
            UUID userId = parseUuid(getValue(userIds, i));
            Object descriptionValue = getValue(descriptions, i);
            String description = descriptionValue != null ? descriptionValue.toString() : "";
            float score = i < scores.size() ? scores.get(i) : 0F;
            results.add(new RealEstateSearchResult(objectId, paramId, userId, score, description));
        }
        return results;
    }

    private static Object invokeRow(Object row, String method, Object... args) throws ReflectiveOperationException {
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i] != null ? args[i].getClass() : Object.class;
        }
        try {
            Method m = row.getClass().getMethod(method, types);
            return m.invoke(row, args);
        } catch (NoSuchMethodException ex) {
            Method m = row.getClass().getMethod(method);
            return m.invoke(row);
        }
    }

    private static List<?> getFieldData(SearchResultsWrapper wrapper, String field) {
        try {
            Method method = wrapper.getClass().getMethod("getFieldData", String.class);
            Object data = method.invoke(wrapper, field);
            if (data instanceof List<?> list) {
                return list;
            }
        } catch (ReflectiveOperationException ignored) {
            // метод может отсутствовать в текущей версии SDK
        }
        return Collections.emptyList();
    }

    private static int getRowCount(SearchResultsWrapper wrapper) {
        try {
            Method method = wrapper.getClass().getMethod("getRowCount");
            Object count = method.invoke(wrapper);
            if (count instanceof Number number) {
                return number.intValue();
            }
        } catch (ReflectiveOperationException ignored) {
            // метод может отсутствовать
        }
        return 0;
    }

    private static Object getValue(List<?> list, int index) {
        if (list == null || list.isEmpty() || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    private static List<Float> getScores(SearchResultsWrapper wrapper) {
        try {
            Method getIdScore = wrapper.getClass().getMethod("getIDScore", int.class);
            Object idScore = getIdScore.invoke(wrapper, 0);
            if (idScore == null) {
                return Collections.emptyList();
            }
            Method getScores = idScore.getClass().getMethod("getScores");
            Object rawScores = getScores.invoke(idScore);
            if (rawScores instanceof List<?> list) {
                List<Float> scores = new ArrayList<>(list.size());
                for (Object value : list) {
                    if (value instanceof Number number) {
                        scores.add(number.floatValue());
                    } else if (value != null) {
                        try {
                            scores.add(Float.parseFloat(value.toString()));
                        } catch (NumberFormatException ignored) {
                            scores.add(0F);
                        }
                    } else {
                        scores.add(0F);
                    }
                }
                return scores;
            }
        } catch (ReflectiveOperationException ignored) {
            // метод может отсутствовать
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private static <B> B invokeBuilder(B builder, Object value, String... methodNames) {
        if (builder == null) {
            throw new IllegalArgumentException("builder must not be null");
        }
        Class<?> builderClass = builder.getClass();
        for (String methodName : methodNames) {
            Method method = findBuilderMethod(builderClass, methodName, value);
            if (method == null) {
                continue;
            }
            try {
                Object argument = coerceValue(method.getParameterTypes()[0], value);
                Object result = method.invoke(builder, argument);
                if (result != null && builderClass.isInstance(result)) {
                    return (B) result;
                }
                return builder;
            } catch (ReflectiveOperationException ex) {
                throw new IllegalStateException("Failed to invoke Milvus builder method '" + methodName + "'", ex);
            }
        }
        throw new IllegalStateException("Milvus SDK builder does not support methods " + Arrays.toString(methodNames));
    }

    private static Method findBuilderMethod(Class<?> builderClass, String methodName, Object value) {
        for (Method method : builderClass.getMethods()) {
            if (!method.getName().equals(methodName) || method.getParameterCount() != 1) {
                continue;
            }
            Class<?> parameterType = method.getParameterTypes()[0];
            if (isParameterCompatible(parameterType, value)) {
                return method;
            }
        }
        return null;
    }

    private static boolean isParameterCompatible(Class<?> parameterType, Object value) {
        if (value == null) {
            return !parameterType.isPrimitive();
        }
        Class<?> targetType = parameterType.isPrimitive() ? primitiveToWrapper(parameterType) : parameterType;
        Class<?> valueType = value.getClass();
        if (Number.class.isAssignableFrom(targetType) && Number.class.isAssignableFrom(valueType)) {
            return true;
        }
        if (Collection.class.isAssignableFrom(targetType)) {
            return value instanceof Collection<?> || value instanceof Iterable<?> || valueType.isArray() || value instanceof String;
        }
        if (targetType.isArray()) {
            return valueType.isArray() || value instanceof Collection<?> || value instanceof Iterable<?> ||
                (value instanceof String && targetType.getComponentType().isAssignableFrom(String.class));
        }
        return targetType.isAssignableFrom(valueType);
    }

    private static Class<?> primitiveToWrapper(Class<?> type) {
        if (type == boolean.class) {
            return Boolean.class;
        } else if (type == byte.class) {
            return Byte.class;
        } else if (type == char.class) {
            return Character.class;
        } else if (type == short.class) {
            return Short.class;
        } else if (type == int.class) {
            return Integer.class;
        } else if (type == long.class) {
            return Long.class;
        } else if (type == float.class) {
            return Float.class;
        } else if (type == double.class) {
            return Double.class;
        }
        return type;
    }

    private static Object coerceValue(Class<?> parameterType, Object value) {
        if (value == null) {
            return null;
        }
        Class<?> targetType = parameterType.isPrimitive() ? primitiveToWrapper(parameterType) : parameterType;
        if (targetType.isInstance(value)) {
            return value;
        }
        if (Number.class.isAssignableFrom(targetType) && value instanceof Number number) {
            if (targetType == Byte.class) {
                return number.byteValue();
            }
            if (targetType == Short.class) {
                return number.shortValue();
            }
            if (targetType == Integer.class) {
                return number.intValue();
            }
            if (targetType == Long.class) {
                return number.longValue();
            }
            if (targetType == Float.class) {
                return number.floatValue();
            }
            if (targetType == Double.class) {
                return number.doubleValue();
            }
        }
        if (Collection.class.isAssignableFrom(targetType)) {
            if (value instanceof Collection<?> collection) {
                return collection;
            }
            if (value instanceof Iterable<?> iterable) {
                List<Object> list = new ArrayList<>();
                for (Object element : iterable) {
                    list.add(element);
                }
                return list;
            }
            if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                List<Object> list = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    list.add(Array.get(value, i));
                }
                return list;
            }
            if (value instanceof String string) {
                return List.of(string);
            }
        }
        if (targetType.isArray()) {
            Class<?> componentType = targetType.getComponentType();
            if (value.getClass().isArray() && targetType.isInstance(value)) {
                return value;
            }
            if (value instanceof Collection<?> collection) {
                Object array = Array.newInstance(componentType, collection.size());
                int index = 0;
                for (Object element : collection) {
                    Array.set(array, index++, coerceValue(componentType, element));
                }
                return array;
            }
            if (value instanceof Iterable<?> iterable) {
                List<Object> list = new ArrayList<>();
                for (Object element : iterable) {
                    list.add(element);
                }
                Object array = Array.newInstance(componentType, list.size());
                for (int i = 0; i < list.size(); i++) {
                    Array.set(array, i, coerceValue(componentType, list.get(i)));
                }
                return array;
            }
            if (value instanceof String string && componentType.isAssignableFrom(String.class)) {
                Object array = Array.newInstance(componentType, 1);
                Array.set(array, 0, string);
                return array;
            }
        }
        return value;
    }

    boolean configureIndexBuilder(CreateIndexParam.Builder builder) {
        if (builder == null) {
            return false;
        }
        boolean applied = applyIndexType(builder);
        if (applied) {
            builder.withExtraParam(properties.getIndexParams());
        }
        return applied;
    }

    private boolean applyIndexType(CreateIndexParam.Builder builder) {
        if (builder == null) {
            return false;
        }
        if (setIndexType(builder, properties.getIndexType())) {
            return true;
        }
        return setIndexType(builder, "IVF_FLAT");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean setIndexType(CreateIndexParam.Builder builder, String type) {
        if (type == null || type.isBlank()) {
            return false;
        }
        String normalized = type.toUpperCase(Locale.ROOT);
        String[] candidates = {"io.milvus.param.index.IndexType", "io.milvus.param.IndexType"};
        for (String className : candidates) {
            try {
                Class<?> indexTypeClass = Class.forName(className);
                if (!Enum.class.isAssignableFrom(indexTypeClass)) {
                    continue;
                }
                Enum value = Enum.valueOf((Class<? extends Enum>) indexTypeClass.asSubclass(Enum.class), normalized);
                Method method = builder.getClass().getMethod("withIndexType", indexTypeClass);
                method.invoke(builder, value);
                return true;
            } catch (ClassNotFoundException ignored) {
                // класс отсутствует в используемой версии SDK
            } catch (IllegalArgumentException ignored) {
                // значение не поддерживается данным перечислением
            } catch (ReflectiveOperationException ignored) {
                // метод не найден или недоступен
            }
        }
        return false;
    }

    private MetricType resolveMetricType() {
        try {
            return MetricType.valueOf(properties.getMetricType().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return MetricType.COSINE;
        }
    }

    private static <T> java.util.stream.Stream<T> streamOf(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static <T> void check(R<T> response, String action) {
        if (response.getStatus() != R.Status.Success.getCode()) {
            throw new IllegalStateException("Failed to " + action + ": " + response.getMessage());
        }
    }

    /**
     * DTO результата поиска по Milvus.
     */
    public record RealEstateSearchResult(UUID objectId, UUID paramId, UUID userId, float score, String description) {
    }

    private record RealEstateVectorRecord(UUID objectId, UUID paramId, UUID userId, String description, float[] vector) {
    }
}
