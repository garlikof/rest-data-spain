package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Представляет переговоры об условиях между предложением и заявкой на аренду.
 */
@Data
@Entity
@Table(name = "negotiation_of_terms")
public class NegotiationOfTerms {
    /**
     * Первичный идентификатор записи о переговорах.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Связанное предложение об аренде.
     */
    @ManyToOne
    @JoinColumn(name = "proposal")
    private RentalProposal proposal;
    /**
     * Связанная заявка на аренду.
     */
    @ManyToOne
    @JoinColumn(name = "claim")
    private RentalClaim claim;
    /**
     * Статус переговоров.
     */
    @Column(name = "status")
    private String status;
}
