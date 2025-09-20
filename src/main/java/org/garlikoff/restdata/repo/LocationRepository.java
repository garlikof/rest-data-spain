package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Repository for {@link Location} entities.
 */
@RepositoryRestResource(path = "locations", collectionResourceRel = "locations")
public interface LocationRepository extends CrudRepository<Location, UUID> {
}
