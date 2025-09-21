package org.garlikoff.restdata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


/**
 * Представляет язык в системе.
 */
@Data
@Entity
@Table(name = "language")
public class Language {
    /**
     * Уникальный ключ языка.
     */
    @Id
    private String key;

}
