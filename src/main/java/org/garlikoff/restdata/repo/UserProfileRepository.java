package org.garlikoff.restdata.repo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.garlikoff.restdata.model.UserProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link UserProfile}.
 */
@Tag(name = "Профили пользователей", description = "Доступ к профилям и персональным данным пользователей.")
@RepositoryRestResource
public interface UserProfileRepository extends CrudRepository<UserProfile, UUID> {
}
