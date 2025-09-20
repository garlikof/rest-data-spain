package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.model.Word;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "words", collectionResourceRel = "words")
public interface WordRepository extends CrudRepository<Word, String> {
}