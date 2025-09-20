package org.garlikoff.restdata.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "word")
public class Word {
    @Id
    @Column(name = "key") String key;
}