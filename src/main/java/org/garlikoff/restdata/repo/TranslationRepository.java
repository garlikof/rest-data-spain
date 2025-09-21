package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.Translation;
import org.garlikoff.restdata.model.TranslationId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Репозиторий для сущностей {@link Translation}.
 */
@RepositoryRestResource(path = "translations", collectionResourceRel = "translations")
public interface TranslationRepository extends CrudRepository<Translation, TranslationId> {
}
