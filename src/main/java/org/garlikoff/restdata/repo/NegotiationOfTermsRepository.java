package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.NegotiationOfTerms;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link NegotiationOfTerms}.
 */
@Tag(name = "Согласования условий", description = "Доступ к сущностям переговоров по условиям аренды.")
@RepositoryRestResource
public interface NegotiationOfTermsRepository extends CrudRepository<NegotiationOfTerms, UUID> {
}
