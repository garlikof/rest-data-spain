package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link User}.
 */
@Tag(name = "Пользователи", description = "Базовые операции с учетными записями пользователей.")
@RepositoryRestResource
public interface UserRepository extends CrudRepository<User, UUID> {
}
