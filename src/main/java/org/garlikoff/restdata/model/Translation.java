package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Представляет перевод слова на заданном языке.
 */
@Data
@Entity
@Table(name = "translation")
public class Translation {
    /**
     * Составной идентификатор, содержащий ключи слова и языка.
     */
    @EmbeddedId
    private TranslationId id;
    /**
     * Локализованное значение перевода.
     */
    @Column(name = "value")
    private String value;
}
