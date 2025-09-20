package org.garlikoff.restdata.repo;

import org.garlikoff.restdata.dto.Language;
import org.springframework.data.repository.CrudRepository;

public interface LanguageRepository extends CrudRepository<Language, String> {
}