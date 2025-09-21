package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.RealEstateObjectParam;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link RealEstateObjectParam}.
 */
@Tag(name = "Параметры объектов недвижимости", description = "Работа с параметрами и характеристиками объектов недвижимости.")
@RepositoryRestResource
public interface RealEstateObjectParamRepository extends CrudRepository<RealEstateObjectParam, UUID> {
}
