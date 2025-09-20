package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Represents a rental proposal for a real estate object.
 */
@Data
@Entity
@Table(name = "rental_proposal")
public class RentalProposal {
    /**
     * Primary identifier for the proposal.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Real estate object associated with the proposal.
     */
    @ManyToOne
    @JoinColumn(name = "real_estate_object_id")
    private RealEstateObject realEstateObject;
    /**
     * The user who created the proposal.
     */
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    /**
     * Location related to the proposal.
     */
    @ManyToOne
    @JoinColumn(name = "location")
    private Location location;
    /**
     * Price of the proposal.
     */
    @Column(name = "price")
    private Double price;
    /**
     * Type of the proposal.
     */
    @Column(name = "type")
    private String type;
    /**
     * Indicates whether pets are allowed.
     */
    @Column(name = "pets")
    private Boolean pets;
    /**
     * Date when the proposal was created.
     */
    @Column(name = "proposal_created")
    private java.sql.Date proposalCreated;
    /**
     * Status of the proposal.
     */
    @Column(name = "proposal_status")
    private String proposalStatus;
}
