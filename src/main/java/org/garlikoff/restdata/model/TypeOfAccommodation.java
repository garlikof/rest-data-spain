package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

/**
 * Represents a type of accommodation.
 */
@Data
@Entity
@Table(name = "type_of_accommodation")
public class TypeOfAccommodation {
    /**
     * Primary identifier of the type.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Name of the accommodation type.
     */
    @Column(name = "name")
    private String name;
}
