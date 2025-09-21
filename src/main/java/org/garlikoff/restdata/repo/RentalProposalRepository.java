package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.RentalProposal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link RentalProposal}.
 */
@RepositoryRestResource(path = "rental-proposals", collectionResourceRel = "rentalProposals")
public interface RentalProposalRepository extends CrudRepository<RentalProposal, UUID> {
}
