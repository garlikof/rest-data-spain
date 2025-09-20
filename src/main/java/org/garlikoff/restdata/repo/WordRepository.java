package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.dto.Word;
import org.springframework.data.repository.CrudRepository;
public interface WordRepository extends CrudRepository<Word, String> {
}