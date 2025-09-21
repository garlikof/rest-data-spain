package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.RentalClaim;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link RentalClaim}.
 */
@Tag(name = "Заявки на аренду", description = "Работа с запросами арендаторов на поиск жилья.")
@RepositoryRestResource
public interface RentalClaimRepository extends CrudRepository<RentalClaim, UUID> {
}
