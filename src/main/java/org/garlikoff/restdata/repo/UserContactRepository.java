package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.UserContact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link UserContact}.
 */
@Tag(name = "Контакты пользователей", description = "Работа с контактной информацией пользователей.")
@RepositoryRestResource
public interface UserContactRepository extends CrudRepository<UserContact, UUID> {
}
