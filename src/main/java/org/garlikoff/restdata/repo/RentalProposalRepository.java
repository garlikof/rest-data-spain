package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.RentalProposal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link RentalProposal}.
 */
@Tag(name = "Предложения аренды", description = "Доступ к предложениям по аренде жилья.")
@RepositoryRestResource
public interface RentalProposalRepository extends CrudRepository<RentalProposal, UUID> {
}
