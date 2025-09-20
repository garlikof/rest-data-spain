package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Links a rental claim to a location.
 */
@Data
@Entity
@Table(name = "rental_claim_location")
public class RentalClaimLocation {
    /**
     * Primary identifier for the relation.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Associated rental claim.
     */
    @ManyToOne
    @JoinColumn(name = "claim")
    private RentalClaim claim;
    /**
     * Associated location.
     */
    @ManyToOne
    @JoinColumn(name = "location")
    private Location location;
}
