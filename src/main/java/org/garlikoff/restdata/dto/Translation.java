package org.garlikoff.restdata.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "translation")
@IdClass(LanguageId.class)
public class Translation {

    @Id
    @ManyToOne
    @JoinColumn(name = "word_key_key")
    Word wordKey;

    @Id
    @ManyToOne
    @JoinColumn(name = "language_key_key")
    Language languageKey;

}