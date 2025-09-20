package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "rental_proposal")
public class RentalProposal {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "real_estate_object_id")
    private RealEstateObject realEstateObject;
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    @ManyToOne
    @JoinColumn(name = "location")
    private Location location;
    @Column(name = "price")
    private Double price;
    @Column(name = "type")
    private String type;
    @Column(name = "pets")
    private Boolean pets;
    @Column(name = "proposal_created")
    private java.sql.Date proposalCreated;
    @Column(name = "proposal_status")
    private String proposalStatus;
}