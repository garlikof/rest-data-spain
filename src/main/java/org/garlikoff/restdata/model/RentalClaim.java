package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "rental_claim")
public class RentalClaim {
    @Id
    @GeneratedValue
    @Column(name = "id") private UUID id;
    @ManyToOne
    @JoinColumn(name = "user") private User user;
    @Column(name = "childs") private Boolean childs;
    @Column(name = "pets") private Boolean pets;
    @Column(name = "price") private String price; // int4range
    @Column(name = "location_ids") private UUID[] locationIds;
    @Column(name = "number_of_residents") private Integer numberOfResidents;
    @Column(name = "number_of_rooms") private Short[] numberOfRooms;
    @Column(name = "date_begin") private java.sql.Date dateBegin;
}
