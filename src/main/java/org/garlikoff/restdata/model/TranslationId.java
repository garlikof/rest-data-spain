package org.garlikoff.restdata.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

/**
 * Composite primary key for the {@link Translation} entity.
 */
@Embeddable
@Data
public class TranslationId implements Serializable {
    /**
     * Word key component of the identifier.
     */
    @Column(name = "word_key")
    private String wordKey;
    /**
     * Language key component of the identifier.
     */
    @Column(name = "language_key")
    private String languageKey;
}
