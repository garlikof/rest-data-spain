package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.UserContact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "user-contacts", collectionResourceRel = "userContacts")
public interface UserContactRepository extends CrudRepository<UserContact, UUID> {
}