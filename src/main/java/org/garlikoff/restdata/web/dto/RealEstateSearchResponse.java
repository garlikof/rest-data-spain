package org.garlikoff.restdata.web.dto;

import java.util.List;

/**
 * Ответ поиска объектов недвижимости по векторной базе данных.
 */
public record RealEstateSearchResponse(List<RealEstateSearchResultDto> results) {
}
