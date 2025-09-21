package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.Word;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Репозиторий для сущностей {@link Word}.
 */
@Tag(name = "Словарь", description = "Управление словарными записями для переводов.")
@RepositoryRestResource
public interface WordRepository extends CrudRepository<Word, String> {
}
