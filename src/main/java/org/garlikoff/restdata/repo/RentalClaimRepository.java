package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.RentalClaim;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link RentalClaim}.
 */
@RepositoryRestResource(path = "rental-claims", collectionResourceRel = "rentalClaims")
public interface RentalClaimRepository extends CrudRepository<RentalClaim, UUID> {
}
