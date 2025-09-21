package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.NegotiationOfTerms;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link NegotiationOfTerms}.
 */
@RepositoryRestResource(path = "negotiations-of-terms", collectionResourceRel = "negotiationsOfTerms")
public interface NegotiationOfTermsRepository extends CrudRepository<NegotiationOfTerms, UUID> {
}
