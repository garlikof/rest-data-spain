package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Represents a rental claim made by a user.
 */
@Data
@Entity
@Table(name = "rental_claim")
public class RentalClaim {
    /**
     * Primary identifier of the claim.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * User who created the claim.
     */
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    /**
     * Indicates whether children are expected.
     */
    @Column(name = "childs")
    private Boolean childs;
    /**
     * Indicates whether pets are expected.
     */
    @Column(name = "pets")
    private Boolean pets;
    /**
     * Desired price range (int4range).
     */
    @Column(name = "price")
    private String price; // int4range
    /**
     * Requested location identifiers.
     */
    @Column(name = "location_ids")
    private UUID[] locationIds;
    /**
     * Number of residents.
     */
    @Column(name = "number_of_residents")
    private Integer numberOfResidents;
    /**
     * Number of rooms required.
     */
    @Column(name = "number_of_rooms")
    private Short[] numberOfRooms;
    /**
     * Desired start date.
     */
    @Column(name = "date_begin")
    private java.sql.Date dateBegin;
}
