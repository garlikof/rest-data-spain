package org.garlikoff.restdata.web;

import org.garlikoff.restdata.service.RealEstateMilvusService;
import org.garlikoff.restdata.service.RealEstateMilvusService.RealEstateSearchResult;
import org.garlikoff.restdata.web.dto.RealEstateSearchResponse;
import org.garlikoff.restdata.web.dto.RealEstateSearchResultDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер, выполняющий поиск объектов недвижимости по запросу.
 */
@RestController
@RequestMapping("/api/vector/real-estate")
public class RealEstateSearchController {

    private final RealEstateMilvusService milvusService;

    public RealEstateSearchController(RealEstateMilvusService milvusService) {
        this.milvusService = milvusService;
    }

    /**
     * Выполняет поиск в Milvus по текстовому запросу.
     *
     * @param query текст поискового запроса
     * @param limit максимальное количество результатов
     * @return подходящие объекты
     */
    @GetMapping("/search")
    public ResponseEntity<RealEstateSearchResponse> search(@RequestParam("query") String query,
                                                           @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<RealEstateSearchResultDto> results = milvusService.search(query, limit).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(new RealEstateSearchResponse(results));
    }

    private RealEstateSearchResultDto toDto(RealEstateSearchResult result) {
        return new RealEstateSearchResultDto(result.objectId(), result.paramId(), result.userId(), result.score(), result.description());
    }
}
