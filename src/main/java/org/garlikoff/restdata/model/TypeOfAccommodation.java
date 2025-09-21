package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

/**
 * Представляет тип жилья.
 */
@Data
@Entity
@Table(name = "type_of_accommodation")
public class TypeOfAccommodation {
    /**
     * Первичный идентификатор типа.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Название типа жилья.
     */
    @Column(name = "name")
    private String name;
}
