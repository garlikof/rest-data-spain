package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.UserProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Repository for {@link UserProfile} entities.
 */
@RepositoryRestResource(path = "user-profiles", collectionResourceRel = "userProfiles")
public interface UserProfileRepository extends CrudRepository<UserProfile, UUID> {
}
