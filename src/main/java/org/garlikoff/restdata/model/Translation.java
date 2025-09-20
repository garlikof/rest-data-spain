package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Entity
@Table(name = "translation")
public class Translation {
    @EmbeddedId private TranslationId id;
    @Column(name = "value") private String value;
}
