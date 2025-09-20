package org.garlikoff.restdata.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class TranslationId implements Serializable {
    @Column(name = "word_key") private String wordKey;
    @Column(name = "language_key") private String languageKey;
}
