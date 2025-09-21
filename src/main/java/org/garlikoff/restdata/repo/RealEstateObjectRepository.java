package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.RealEstateObject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link RealEstateObject}.
 */
@Tag(name = "Объекты недвижимости", description = "CRUD операции с объектами недвижимости.")
@RepositoryRestResource
public interface RealEstateObjectRepository extends CrudRepository<RealEstateObject, UUID> {
}
