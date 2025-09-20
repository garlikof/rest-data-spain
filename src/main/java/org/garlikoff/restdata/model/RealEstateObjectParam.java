package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "real_estate_object_param")
public class RealEstateObjectParam {
    @Id
    @GeneratedValue
    @Column(name = "id") private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id") private User user;
    @ManyToOne
    @JoinColumn(name = "location") private Location location;
    @Column(name = "area") private Double area;
    @Column(name = "number_of_bedrooms") private Integer numberOfBedrooms;
    @Column(name = "number_of_bathrooms") private Integer numberOfBathrooms;
    @Column(name = "type") private String type;
    @Column(name = "furnishings") private String furnishings;
    @Column(name = "elevator") private Boolean elevator;
    @Column(name = "balcony") private Boolean balcony;
    @Column(name = "garage") private Boolean garage;
    @Column(name = "courtyard") private Boolean courtyard;
    @Column(name = "pool") private Boolean pool;
    @Column(name = "storeroom") private Boolean storeroom;
    @Column(name = "floor") private Integer floor;
    @Column(name = "air_conditioner") private Boolean airConditioner;
}
