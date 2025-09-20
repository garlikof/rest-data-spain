package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Detailed parameters for a real estate object.
 */
@Data
@Entity
@Table(name = "real_estate_object_param")
public class RealEstateObjectParam {
    /**
     * Primary identifier for the parameter set.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Owner of the property.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    /**
     * Location of the property.
     */
    @ManyToOne
    @JoinColumn(name = "location")
    private Location location;
    /**
     * Area of the property.
     */
    @Column(name = "area")
    private Double area;
    /**
     * Number of bedrooms.
     */
    @Column(name = "number_of_bedrooms")
    private Integer numberOfBedrooms;
    /**
     * Number of bathrooms.
     */
    @Column(name = "number_of_bathrooms")
    private Integer numberOfBathrooms;
    /**
     * Type of accommodation.
     */
    @Column(name = "type")
    private String type;
    /**
     * Furnishing details.
     */
    @Column(name = "furnishings")
    private String furnishings;
    /**
     * Whether an elevator is available.
     */
    @Column(name = "elevator")
    private Boolean elevator;
    /**
     * Whether a balcony is available.
     */
    @Column(name = "balcony")
    private Boolean balcony;
    /**
     * Whether a garage is available.
     */
    @Column(name = "garage")
    private Boolean garage;
    /**
     * Whether a courtyard is available.
     */
    @Column(name = "courtyard")
    private Boolean courtyard;
    /**
     * Whether a pool is available.
     */
    @Column(name = "pool")
    private Boolean pool;
    /**
     * Whether a storeroom is available.
     */
    @Column(name = "storeroom")
    private Boolean storeroom;
    /**
     * Floor number.
     */
    @Column(name = "floor")
    private Integer floor;
    /**
     * Indicates availability of an air conditioner.
     */
    @Column(name = "air_conditioner")
    private Boolean airConditioner;
}
