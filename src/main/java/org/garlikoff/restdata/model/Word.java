package org.garlikoff.restdata.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Представляет словарное слово.
 */
@Data
@Entity
@Table(name = "word")
public class Word {
    /**
     * Уникальный ключ слова.
     */
    @Id
    @Column(name = "key")
    private String key;
}
