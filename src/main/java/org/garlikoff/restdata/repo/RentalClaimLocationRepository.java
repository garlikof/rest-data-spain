package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.RentalClaimLocation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link RentalClaimLocation}.
 */
@Tag(name = "Локации заявок на аренду", description = "Управление привязкой заявок на аренду к локациям.")
@RepositoryRestResource
public interface RentalClaimLocationRepository extends CrudRepository<RentalClaimLocation, UUID> {
}
