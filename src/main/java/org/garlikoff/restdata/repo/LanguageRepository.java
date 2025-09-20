package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.Language;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository for {@link Language} entities.
 */
@RepositoryRestResource(path = "languages", collectionResourceRel = "languages")
public interface LanguageRepository extends CrudRepository<Language, String> {
}
