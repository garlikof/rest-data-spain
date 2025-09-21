package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Связывает заявку на аренду с местоположением.
 */
@Data
@Entity
@Table(name = "rental_claim_location")
public class RentalClaimLocation {
    /**
     * Первичный идентификатор связи.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Связанная заявка на аренду.
     */
    @ManyToOne
    @JoinColumn(name = "claim")
    private RentalClaim claim;
    /**
     * Связанное местоположение.
     */
    @ManyToOne
    @JoinColumn(name = "location")
    private Location location;
}
