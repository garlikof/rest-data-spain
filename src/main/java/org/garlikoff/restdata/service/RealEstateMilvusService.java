package org.garlikoff.restdata.service;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.R;
import io.milvus.param.collection.CollectionSchema;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.FlushParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.IndexType;
import io.milvus.param.metric.MetricType;
import io.milvus.response.SearchResultsWrapper;
import io.milvus.response.R.Status;
import io.milvus.grpc.FlushResponse;
import io.milvus.grpc.RpcStatus;
import org.garlikoff.restdata.config.MilvusProperties;
import org.garlikoff.restdata.model.RealEstateObject;
import org.garlikoff.restdata.model.RealEstateObjectParam;
import org.garlikoff.restdata.model.TranslationId;
import org.garlikoff.restdata.repo.RealEstateObjectParamRepository;
import org.garlikoff.restdata.repo.RealEstateObjectRepository;
import org.garlikoff.restdata.repo.TranslationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
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
        SearchParam searchParam = SearchParam.newBuilder()
            .withCollectionName(properties.getCollection())
            .withMetricType(resolveMetricType())
            .withOutFields(List.of(FIELD_OBJECT_ID, FIELD_PARAM_ID, FIELD_USER_ID, FIELD_DESCRIPTION))
            .withTopK(Math.max(1, limit))
            .withVectors(vectors)
            .withVectorFieldName(properties.getVectorField())
            .withParams(properties.getSearchParams())
            .build();
        io.milvus.param.R<io.milvus.grpc.SearchResults> response = client.search(searchParam);
        check(response, "search vectors");
        SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());
        List<SearchResultsWrapper.RowRecord> rows = wrapper.getRowRecords();
        List<RealEstateSearchResult> results = new ArrayList<>(rows.size());
        for (SearchResultsWrapper.RowRecord row : rows) {
            UUID objectId = parseUuid(row.getFieldValue(FIELD_OBJECT_ID));
            UUID paramId = parseUuid(row.getFieldValue(FIELD_PARAM_ID));
            UUID userId = parseUuid(row.getFieldValue(FIELD_USER_ID));
            String description = Optional.ofNullable(row.getFieldValue(FIELD_DESCRIPTION))
                .map(Object::toString)
                .orElse("");
            results.add(new RealEstateSearchResult(objectId, paramId, userId, row.getScore(), description));
        }
        return results;
    }

    private synchronized void ensureCollection() {
        HasCollectionParam hasCollectionParam = HasCollectionParam.newBuilder()
            .withCollectionName(properties.getCollection())
            .build();
        R<Boolean> exists = client.hasCollection(hasCollectionParam);
        check(exists, "check collection existence");
        if (Boolean.TRUE.equals(exists.getData())) {
            return;
        }
        createCollection();
    }

    private synchronized void recreateCollection() {
        HasCollectionParam hasCollectionParam = HasCollectionParam.newBuilder()
            .withCollectionName(properties.getCollection())
            .build();
        R<Boolean> exists = client.hasCollection(hasCollectionParam);
        check(exists, "check collection existence");
        if (Boolean.TRUE.equals(exists.getData())) {
            dropCollection();
        }
        createCollection();
    }

    private void dropCollection() {
        io.milvus.param.collection.DropCollectionParam dropCollectionParam = io.milvus.param.collection.DropCollectionParam.newBuilder()
            .withCollectionName(properties.getCollection())
            .build();
        R<RpcStatus> dropResponse = client.dropCollection(dropCollectionParam);
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
        CollectionSchema schema = CollectionSchema.newBuilder()
            .addFieldType(primary)
            .addFieldType(objectId)
            .addFieldType(paramId)
            .addFieldType(userId)
            .addFieldType(description)
            .addFieldType(vector)
            .build();
        CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
            .withCollectionName(properties.getCollection())
            .withDescription("Real estate objects embeddings")
            .withShardsNum(properties.getShardsNum())
            .withSchema(schema)
            .build();
        R<RpcStatus> createResponse = client.createCollection(createCollectionParam);
        check(createResponse, "create collection");
        CreateIndexParam indexParam = CreateIndexParam.newBuilder()
            .withCollectionName(properties.getCollection())
            .withFieldName(properties.getVectorField())
            .withIndexName(properties.getVectorField() + "_idx")
            .withIndexType(resolveIndexType())
            .withMetricType(resolveMetricType())
            .withExtraParam(properties.getIndexParams())
            .build();
        R<RpcStatus> indexResponse = client.createIndex(indexParam);
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
        InsertParam insertParam = InsertParam.newBuilder()
            .withCollectionName(properties.getCollection())
            .withFields(fields)
            .build();
        R<io.milvus.grpc.MutationResult> insertResponse = client.insert(insertParam);
        check(insertResponse, "insert records");
        R<FlushResponse> flushResponse = client.flush(FlushParam.newBuilder()
            .withCollectionName(properties.getCollection())
            .build());
        check(flushResponse, "flush collection");
        R<RpcStatus> loadResponse = client.loadCollection(LoadCollectionParam.newBuilder()
            .withCollectionName(properties.getCollection())
            .build());
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

    private IndexType resolveIndexType() {
        try {
            return IndexType.valueOf(properties.getIndexType().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return IndexType.IVF_FLAT;
        }
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
        if (response.getStatus() != Status.Success.getCode()) {
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
