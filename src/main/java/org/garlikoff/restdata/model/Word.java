package org.garlikoff.restdata.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Represents a dictionary word.
 */
@Data
@Entity
@Table(name = "word")
public class Word {
    /**
     * Unique key for the word.
     */
    @Id
    @Column(name = "key")
    private String key;
}
