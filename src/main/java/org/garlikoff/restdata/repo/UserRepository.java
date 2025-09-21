package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Репозиторий для сущностей {@link User}.
 */
@RepositoryRestResource(path = "users", collectionResourceRel = "users")
public interface UserRepository extends CrudRepository<User, UUID> {
}
