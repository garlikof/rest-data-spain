package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Represents a geographical location.
 */
@Data
@Entity
@Table(name = "location")
public class Location {
    /**
     * Primary identifier for the location.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Reference key to translation of the location name.
     */
    @Column(name = "name_key")
    private String nameKey;
    /**
     * Type of the location.
     */
    @Column(name = "type")
    private String type;
    /**
     * Parent location, if any.
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Location parent;
}
