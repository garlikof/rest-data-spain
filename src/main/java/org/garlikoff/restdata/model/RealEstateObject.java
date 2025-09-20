package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Represents a real estate object belonging to a user.
 */
@Data
@Entity
@Table(name = "real_estate_object")
public class RealEstateObject {
    /**
     * Primary identifier of the real estate object.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Owner of the real estate object.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
