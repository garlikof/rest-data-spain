package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents a translation for a word in a given language.
 */
@Data
@Entity
@Table(name = "translation")
public class Translation {
    /**
     * Composite identifier containing word and language keys.
     */
    @EmbeddedId
    private TranslationId id;
    /**
     * Localized value of the translation.
     */
    @Column(name = "value")
    private String value;
}
