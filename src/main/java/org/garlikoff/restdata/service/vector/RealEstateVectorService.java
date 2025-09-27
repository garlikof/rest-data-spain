package org.garlikoff.restdata.service.vector;

import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.exception.MilvusException;
import io.milvus.grpc.SearchResults;
import io.milvus.param.*;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.DropCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.collection.ReleaseCollectionParam;
import io.milvus.param.collection.FlushParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.metric.MetricType;
import io.milvus.response.R;
import io.milvus.response.SearchResultsWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.garlikoff.restdata.config.MilvusProperties;
import org.garlikoff.restdata.model.Location;
import org.garlikoff.restdata.model.RealEstateObject;
import org.garlikoff.restdata.model.RealEstateObjectParam;
import org.garlikoff.restdata.model.TypeOfAccommodation;
import org.garlikoff.restdata.repo.RealEstateObjectParamRepository;
import org.garlikoff.restdata.repo.RealEstateObjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Сервис для выгрузки и поиска объектов недвижимости в Milvus.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RealEstateVectorService {

    private static final String PRIMARY_FIELD = "vector_id";
    private static final String VECTOR_FIELD = "embedding";
    private static final String ENTITY_ID_FIELD = "entity_id";
    private static final String ENTITY_TYPE_FIELD = "entity_type";
    private static final String SUMMARY_FIELD = "summary";

    private final MilvusServiceClient milvusClient;
    private final MilvusProperties properties;
    private final TextEmbeddingService embeddingService;
    private final RealEstateObjectRepository realEstateObjectRepository;
    private final RealEstateObjectParamRepository realEstateObjectParamRepository;

    @PostConstruct
    public void ensureCollectionExists() {
        if (!properties.isAutoCreateCollection()) {
            return;
        }
        try {
            boolean hasCollection = Boolean.TRUE.equals(milvusClient.hasCollection(
                    HasCollectionParam.newBuilder().withCollectionName(properties.getCollectionName()).build()).getData());
            if (!hasCollection) {
                log.info("Milvus collection '{}' not found. Creating...", properties.getCollectionName());
                createCollection();
            }
        } catch (MilvusException e) {
            throw new IllegalStateException("Failed to initialize Milvus collection", e);
        }
    }

    /**
     * Полностью пересобирает коллекцию Milvus из актуальных данных базы.
     */
    @Transactional(readOnly = true)
    public synchronized void synchronizeAll() {
        dropCollectionIfExists();
        createCollection();

        List<String> vectorIds = new ArrayList<>();
        List<List<Float>> embeddings = new ArrayList<>();
        List<String> entityIds = new ArrayList<>();
        List<String> entityTypes = new ArrayList<>();
        List<String> summaries = new ArrayList<>();

        realEstateObjectRepository.findAll().forEach(object -> {
            String summary = buildObjectSummary(object);
            vectorIds.add("object:" + object.getId());
            embeddings.add(embeddingService.embed(summary));
            entityIds.add(object.getId() != null ? object.getId().toString() : "");
            entityTypes.add("REAL_ESTATE_OBJECT");
            summaries.add(summary);
        });

        realEstateObjectParamRepository.findAll().forEach(param -> {
            String summary = buildParamSummary(param);
            vectorIds.add("param:" + param.getId());
            embeddings.add(embeddingService.embed(summary));
            entityIds.add(param.getId() != null ? param.getId().toString() : "");
            entityTypes.add("REAL_ESTATE_OBJECT_PARAM");
            summaries.add(summary);
        });

        if (vectorIds.isEmpty()) {
            log.info("No real estate data found for synchronization with Milvus.");
            return;
        }

        List<InsertParam.Field> fields = List.of(
                new InsertParam.Field(PRIMARY_FIELD, vectorIds),
                new InsertParam.Field(VECTOR_FIELD, embeddings),
                new InsertParam.Field(ENTITY_ID_FIELD, entityIds),
                new InsertParam.Field(ENTITY_TYPE_FIELD, entityTypes),
                new InsertParam.Field(SUMMARY_FIELD, summaries)
        );

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(properties.getCollectionName())
                .withFields(fields)
                .build();

        milvusClient.insert(insertParam);
        milvusClient.flush(FlushParam.newBuilder()
                .withCollectionNames(Collections.singletonList(properties.getCollectionName()))
                .build());
        log.info("Synchronized {} records with Milvus collection {}", vectorIds.size(), properties.getCollectionName());
    }

    /**
     * Выполняет поиск по текстовому запросу среди объектов недвижимости.
     *
     * @param query строка запроса
     * @param limit максимальное количество результатов
     * @return список результатов поиска
     */
    public List<RealEstateVectorSearchResult> search(String query, int limit) {
        if (!StringUtils.hasText(query)) {
            return Collections.emptyList();
        }

        loadCollection();

        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(properties.getCollectionName())
                .withMetricType(MetricType.L2)
                .withOutFields(List.of(PRIMARY_FIELD, ENTITY_ID_FIELD, ENTITY_TYPE_FIELD, SUMMARY_FIELD))
                .withTopK(limit)
                .withVectors(Collections.singletonList(embeddingService.embed(query)))
                .withVectorFieldName(VECTOR_FIELD)
                .withConsistencyLevel(ConsistencyLevelEnum.BOUNDED)
                .build();

        R<SearchResults> result = milvusClient.search(searchParam);
        if (result.getData() == null) {
            return Collections.emptyList();
        }
        SearchResultsWrapper wrapper = new SearchResultsWrapper(result.getData().getResults());

        List<SearchResultsWrapper.IDScore> scores = wrapper.getIDScore(0);
        List<?> entityIdField = wrapper.getFieldData(ENTITY_ID_FIELD, 0);
        List<?> entityTypeField = wrapper.getFieldData(ENTITY_TYPE_FIELD, 0);
        List<?> summaryField = wrapper.getFieldData(SUMMARY_FIELD, 0);

        List<RealEstateVectorSearchResult> responses = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            SearchResultsWrapper.IDScore score = scores.get(i);
            String entityIdValue = entityIdField != null && entityIdField.size() > i ? (String) entityIdField.get(i) : null;
            String entityTypeValue = entityTypeField != null && entityTypeField.size() > i ? (String) entityTypeField.get(i) : null;
            String summaryValue = summaryField != null && summaryField.size() > i ? (String) summaryField.get(i) : null;
            UUID entityUuid = parseUuid(entityIdValue);
            responses.add(buildResult(score.getStrID(), entityUuid, entityTypeValue, score.getScore(), summaryValue));
        }
        return responses;
    }

    private RealEstateVectorSearchResult buildResult(String vectorId, UUID entityUuid, String entityType,
                                                     double score, String summary) {
        RealEstateVectorSearchResult.RealEstateVectorSearchResultBuilder builder = RealEstateVectorSearchResult.builder()
                .vectorId(vectorId)
                .entityType(entityType)
                .score(score)
                .summary(summary);
        if (entityUuid != null) {
            builder.entityId(entityUuid);
            if ("REAL_ESTATE_OBJECT".equals(entityType)) {
                realEstateObjectRepository.findById(entityUuid)
                        .ifPresent(object -> builder.details(Collections.singletonMap("userId",
                                object.getUser() != null ? object.getUser().getId() : null)));
            } else if ("REAL_ESTATE_OBJECT_PARAM".equals(entityType)) {
                realEstateObjectParamRepository.findById(entityUuid)
                        .ifPresent(param -> builder.details(buildParamDetails(param)));
            }
        }
        return builder.build();
    }

    private UUID parseUuid(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            log.warn("Unable to parse UUID '{}' returned by Milvus", value);
            return null;
        }
    }

    private void loadCollection() {
        try {
            milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                    .withCollectionName(properties.getCollectionName())
                    .build());
        } catch (MilvusException e) {
            throw new IllegalStateException("Failed to load Milvus collection", e);
        }
    }

    private void dropCollectionIfExists() {
        try {
            boolean hasCollection = Boolean.TRUE.equals(milvusClient.hasCollection(HasCollectionParam.newBuilder()
                    .withCollectionName(properties.getCollectionName()).build()).getData());
            if (hasCollection) {
                milvusClient.releaseCollection(ReleaseCollectionParam.newBuilder()
                        .withCollectionName(properties.getCollectionName())
                        .build());
                milvusClient.dropCollection(DropCollectionParam.newBuilder()
                        .withCollectionName(properties.getCollectionName())
                        .build());
            }
        } catch (MilvusException e) {
            throw new IllegalStateException("Failed to drop Milvus collection", e);
        }
    }

    private void createCollection() {
        FieldType vectorIdField = FieldType.newBuilder()
                .withName(PRIMARY_FIELD)
                .withDataType(DataType.VarChar)
                .withMaxLength(128)
                .withPrimaryKey(true)
                .withAutoID(false)
                .build();

        FieldType vectorField = FieldType.newBuilder()
                .withName(VECTOR_FIELD)
                .withDataType(DataType.FloatVector)
                .withDimension(properties.getDimension())
                .build();

        FieldType entityIdField = FieldType.newBuilder()
                .withName(ENTITY_ID_FIELD)
                .withDataType(DataType.VarChar)
                .withMaxLength(64)
                .build();

        FieldType entityTypeField = FieldType.newBuilder()
                .withName(ENTITY_TYPE_FIELD)
                .withDataType(DataType.VarChar)
                .withMaxLength(64)
                .build();

        FieldType summaryField = FieldType.newBuilder()
                .withName(SUMMARY_FIELD)
                .withDataType(DataType.VarChar)
                .withMaxLength(2048)
                .build();

        CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                .withCollectionName(properties.getCollectionName())
                .withFieldTypes(List.of(vectorIdField, vectorField, entityIdField, entityTypeField, summaryField))
                .build();
        try {
            milvusClient.createCollection(createCollectionParam);
        } catch (MilvusException e) {
            throw new IllegalStateException("Failed to create Milvus collection", e);
        }
    }

    private String buildObjectSummary(RealEstateObject object) {
        StringBuilder summary = new StringBuilder("Real estate object");
        if (object.getUser() != null && object.getUser().getId() != null) {
            summary.append(" owned by user ").append(object.getUser().getId());
        }
        return summary.toString();
    }

    private String buildParamSummary(RealEstateObjectParam param) {
        StringBuilder summary = new StringBuilder("Real estate details");
        appendIfNotNull(summary, " area", formatNumber(param.getArea()))
                .append(parameterValue(" bedrooms", param.getNumberOfBedrooms()))
                .append(parameterValue(" bathrooms", param.getNumberOfBathrooms()))
                .append(parameterValue(" floor", param.getFloor()))
                .append(parameterValue(" rooms", param.getNumberOfRooms()))
                .append(parameterValue(" furnishings", param.getFurnishings()))
                .append(booleanParameter(" elevator", param.getElevator()))
                .append(booleanParameter(" balcony", param.getBalcony()))
                .append(booleanParameter(" garage", param.getGarage()))
                .append(booleanParameter(" courtyard", param.getCourtyard()))
                .append(booleanParameter(" pool", param.getPool()))
                .append(booleanParameter(" storeroom", param.getStoreroom()))
                .append(booleanParameter(" air conditioner", param.getAirConditioner()))
                .append(booleanParameter(" children", param.getChildrenAllowed()))
                .append(booleanParameter(" pets", param.getPetsAllowed()));
        if (param.getTypeOfAccommodation() != null) {
            TypeOfAccommodation type = param.getTypeOfAccommodation();
            if (StringUtils.hasText(type.getName())) {
                summary.append(" type ").append(type.getName());
            }
        }
        if (param.getLocation() != null) {
            Location location = param.getLocation();
            summary.append(" location ").append(location.getNameKey());
            if (StringUtils.hasText(location.getType())) {
                summary.append(" type ").append(location.getType());
            }
        }
        if (param.getRealEstateObject() != null && param.getRealEstateObject().getId() != null) {
            summary.append(" object ").append(param.getRealEstateObject().getId());
        }
        appendIfNotNull(summary, " price", formatMoney(param.getPrice(), param.getCurrency()));
        appendIfNotNull(summary, " deposit", formatMoney(param.getDeposit(), param.getCurrency()));
        appendIfNotNull(summary, " available from", formatLocalDate(param.getAvailableFrom()));
        appendIfNotNull(summary, " description", shorten(param.getDescription(), 120));
        return summary.toString();
    }

    private StringBuilder appendIfNotNull(StringBuilder builder, String label, String value) {
        if (value != null) {
            builder.append(label).append(' ').append(value);
        }
        return builder;
    }

    private String parameterValue(String label, Number value) {
        if (value == null) {
            return "";
        }
        return label + " " + value;
    }

    private String parameterValue(String label, String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return label + " " + value;
    }

    private String booleanParameter(String label, Boolean value) {
        if (value == null) {
            return "";
        }
        return label + (Boolean.TRUE.equals(value) ? " yes" : " no");
    }

    private String formatMoney(java.math.BigDecimal value, String currency) {
        if (value == null) {
            return null;
        }
        String amount = value.stripTrailingZeros().toPlainString();
        if (StringUtils.hasText(currency)) {
            return amount + " " + currency;
        }
        return amount;
    }

    private String formatLocalDate(java.time.LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.toString();
    }

    private String shorten(String text, int maxLength) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        String trimmed = text.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength) + "…";
    }

    private String formatNumber(Double value) {
        if (value == null) {
            return null;
        }
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private java.util.Map<String, Object> buildParamDetails(RealEstateObjectParam param) {
        java.util.Map<String, Object> details = new java.util.HashMap<>();
        if (param.getRealEstateObject() != null) {
            details.put("realEstateObjectId", param.getRealEstateObject().getId());
        }
        details.put("area", param.getArea());
        details.put("bedrooms", param.getNumberOfBedrooms());
        details.put("bathrooms", param.getNumberOfBathrooms());
        if (param.getTypeOfAccommodation() != null) {
            details.put("typeOfAccommodationId", param.getTypeOfAccommodation().getId());
            details.put("typeOfAccommodation", param.getTypeOfAccommodation().getName());
        }
        details.put("furnishings", param.getFurnishings());
        details.put("rooms", param.getNumberOfRooms());
        details.put("price", param.getPrice());
        details.put("currency", param.getCurrency());
        details.put("deposit", param.getDeposit());
        details.put("availableFrom", param.getAvailableFrom());
        details.put("childrenAllowed", param.getChildrenAllowed());
        details.put("petsAllowed", param.getPetsAllowed());
        details.put("description", param.getDescription());
        if (param.getLocation() != null) {
            details.put("location", param.getLocation().getNameKey());
            details.put("locationId", param.getLocation().getId());
        }
        return details;
    }
}
