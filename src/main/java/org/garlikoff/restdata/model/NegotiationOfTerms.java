package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "negotiation_of_terms")
public class NegotiationOfTerms {
    @Id
    @GeneratedValue
    @Column(name = "id") private UUID id;
    @ManyToOne
    @JoinColumn(name = "proposal") private RentalProposal proposal;
    @ManyToOne
    @JoinColumn(name = "claim") private RentalClaim claim;
    @Column(name = "status") private String status;
}