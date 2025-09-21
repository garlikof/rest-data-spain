package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link Location}.
 */
@Tag(name = "Локации", description = "CRUD операции со справочником географических локаций.")
@RepositoryRestResource
public interface LocationRepository extends CrudRepository<Location, UUID> {
}
