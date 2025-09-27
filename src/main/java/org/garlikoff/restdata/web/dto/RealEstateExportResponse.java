package org.garlikoff.restdata.web.dto;

/**
 * Ответ на запрос экспорта объектов недвижимости в Milvus.
 */
public record RealEstateExportResponse(int totalExported) {
}
