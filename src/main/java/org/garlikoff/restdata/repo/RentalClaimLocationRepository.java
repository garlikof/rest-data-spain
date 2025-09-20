package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.RentalClaimLocation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Repository for {@link RentalClaimLocation} entities.
 */
@RepositoryRestResource(path = "rental-claim-locations", collectionResourceRel = "rentalClaimLocations")
public interface RentalClaimLocationRepository extends CrudRepository<RentalClaimLocation, UUID> {
}
