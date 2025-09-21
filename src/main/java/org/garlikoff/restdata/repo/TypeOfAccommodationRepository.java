package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.TypeOfAccommodation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link TypeOfAccommodation}.
 */
@Tag(name = "Типы размещения", description = "Управление справочником типов жилья.")
@RepositoryRestResource
public interface TypeOfAccommodationRepository extends CrudRepository<TypeOfAccommodation, UUID> {
}
