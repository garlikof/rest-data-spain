package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "translation")
public class Translation {
    @EmbeddedId private TranslationId id;
    @Column(name = "value") private String value;
}
