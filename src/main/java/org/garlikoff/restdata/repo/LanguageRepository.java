package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.Language;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Репозиторий для сущностей {@link Language}.
 */
@Tag(name = "Языки", description = "Управление справочником доступных языков.")
@RepositoryRestResource
public interface LanguageRepository extends CrudRepository<Language, String> {
}
