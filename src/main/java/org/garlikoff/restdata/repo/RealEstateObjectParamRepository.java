package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.RealEstateObjectParam;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "real-estate-object-params", collectionResourceRel = "realEstateObjectParams")
public interface RealEstateObjectParamRepository extends CrudRepository<RealEstateObjectParam, UUID> {
}