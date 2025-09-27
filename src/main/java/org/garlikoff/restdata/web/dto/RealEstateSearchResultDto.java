package org.garlikoff.restdata.web.dto;

import java.util.UUID;

/**
 * DTO результата поиска объекта недвижимости.
 */
public record RealEstateSearchResultDto(UUID objectId, UUID paramId, UUID userId, float score, String description) {
}
