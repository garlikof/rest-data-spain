package org.garlikoff.restdata.service.vector;

import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.UUID;

/**
 * Результат поиска по объектам недвижимости в Milvus.
 */
@Value
@Builder
public class RealEstateVectorSearchResult {
    String vectorId;
    UUID entityId;
    String entityType;
    double score;
    String summary;
    Map<String, Object> details;
}
