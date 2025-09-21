package org.garlikoff.restdata.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

/**
 * Составной первичный ключ сущности {@link Translation}.
 */
@Embeddable
@Data
public class TranslationId implements Serializable {
    /**
     * Компонент идентификатора с ключом слова.
     */
    @Column(name = "word_key")
    private String wordKey;
    /**
     * Компонент идентификатора с ключом языка.
     */
    @Column(name = "language_key")
    private String languageKey;
}
