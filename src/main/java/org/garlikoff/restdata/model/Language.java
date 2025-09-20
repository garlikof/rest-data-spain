package org.garlikoff.restdata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


/**
 * Represents a language in the system.
 */
@Data
@Entity
@Table(name = "language")
public class Language {
    /**
     * Unique key of the language.
     */
    @Id
    private String key;

}
