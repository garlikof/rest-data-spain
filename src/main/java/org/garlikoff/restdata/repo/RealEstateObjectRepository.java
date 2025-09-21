package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.RealEstateObject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link RealEstateObject}.
 */
@RepositoryRestResource(path = "real-estate-objects", collectionResourceRel = "realEstateObjects")
public interface RealEstateObjectRepository extends CrudRepository<RealEstateObject, UUID> {
}
