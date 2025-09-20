package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.RentalClaim;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Repository for {@link RentalClaim} entities.
 */
@RepositoryRestResource(path = "rental-claims", collectionResourceRel = "rentalClaims")
public interface RentalClaimRepository extends CrudRepository<RentalClaim, UUID> {
}
