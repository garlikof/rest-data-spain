package org.garlikoff.restdata.web;

import org.garlikoff.restdata.service.RealEstateMilvusService;
import org.garlikoff.restdata.web.dto.RealEstateExportResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер, запускающий выгрузку объектов недвижимости в Milvus.
 */
@RestController
@RequestMapping("/api/vector/real-estate")
public class RealEstateExportController {

    private final RealEstateMilvusService milvusService;

    public RealEstateExportController(RealEstateMilvusService milvusService) {
        this.milvusService = milvusService;
    }

    /**
     * Выполняет полную синхронизацию объектов недвижимости в Milvus.
     *
     * @return количество обработанных объектов
     */
    @PostMapping("/export")
    public ResponseEntity<RealEstateExportResponse> export() {
        int total = milvusService.synchronizeAll();
        return ResponseEntity.ok(new RealEstateExportResponse(total));
    }
}
