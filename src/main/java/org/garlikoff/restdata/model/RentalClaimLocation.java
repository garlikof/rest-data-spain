package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "rental_claim_location")
public class RentalClaimLocation {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "claim")
    private RentalClaim claim;
    @ManyToOne
    @JoinColumn(name = "location")
    private Location location;
}