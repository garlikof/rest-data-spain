package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.dto.LanguageId;
import org.garlikoff.restdata.dto.Translation;
import org.springframework.data.repository.CrudRepository;

public interface TranslatonRepository extends CrudRepository<Translation, LanguageId> {
}