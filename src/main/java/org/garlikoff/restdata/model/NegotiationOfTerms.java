package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Represents a negotiation of terms between a rental proposal and claim.
 */
@Data
@Entity
@Table(name = "negotiation_of_terms")
public class NegotiationOfTerms {
    /**
     * Primary identifier for the negotiation record.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Linked rental proposal.
     */
    @ManyToOne
    @JoinColumn(name = "proposal")
    private RentalProposal proposal;
    /**
     * Linked rental claim.
     */
    @ManyToOne
    @JoinColumn(name = "claim")
    private RentalClaim claim;
    /**
     * Negotiation status.
     */
    @Column(name = "status")
    private String status;
}
