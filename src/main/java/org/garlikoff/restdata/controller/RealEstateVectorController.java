package org.garlikoff.restdata.controller;

import lombok.RequiredArgsConstructor;
import org.garlikoff.restdata.service.vector.RealEstateVectorSearchResult;
import org.garlikoff.restdata.service.vector.RealEstateVectorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST контроллер для управления выгрузкой данных в Milvus и поиска.
 */
@RestController
@RequestMapping("/api/v1/real-estate/vector")
@RequiredArgsConstructor
public class RealEstateVectorController {

    private final RealEstateVectorService realEstateVectorService;

    /**
     * Выполняет полную синхронизацию объектов недвижимости с Milvus.
     */
    @PostMapping("/sync")
    public ResponseEntity<Void> synchronize() {
        realEstateVectorService.synchronizeAll();
        return ResponseEntity.accepted().build();
    }

    /**
     * Ищет объекты недвижимости по текстовому запросу.
     */
    @GetMapping("/search")
    public ResponseEntity<List<RealEstateVectorSearchResult>> search(
            @RequestParam("query") String query,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(realEstateVectorService.search(query, limit));
    }
}
