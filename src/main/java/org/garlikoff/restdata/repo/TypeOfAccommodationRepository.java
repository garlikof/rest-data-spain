package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.TypeOfAccommodation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "types-of-accommodation", collectionResourceRel = "typesOfAccommodation")
public interface TypeOfAccommodationRepository extends CrudRepository<TypeOfAccommodation, UUID> {
}