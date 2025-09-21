package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.Translation;
import org.garlikoff.restdata.model.TranslationId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Репозиторий для сущностей {@link Translation}.
 */
@Tag(name = "Переводы", description = "CRUD операции с переводами текстов пользовательского интерфейса.")
@RepositoryRestResource
public interface TranslationRepository extends CrudRepository<Translation, TranslationId> {
}
