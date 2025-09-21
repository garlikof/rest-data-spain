package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Подробные параметры объекта недвижимости.
 */
@Data
@Entity
@Table(name = "real_estate_object_param")
public class RealEstateObjectParam {
    /**
     * Первичный идентификатор набора параметров.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Владелец недвижимости.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    /**
     * Местоположение недвижимости.
     */
    @ManyToOne
    @JoinColumn(name = "location")
    private Location location;
    /**
     * Площадь недвижимости.
     */
    @Column(name = "area")
    private Double area;
    /**
     * Количество спален.
     */
    @Column(name = "number_of_bedrooms")
    private Integer numberOfBedrooms;
    /**
     * Количество ванных комнат.
     */
    @Column(name = "number_of_bathrooms")
    private Integer numberOfBathrooms;
    /**
     * Тип жилья.
     */
    @Column(name = "type")
    private String type;
    /**
     * Сведения о меблировке.
     */
    @Column(name = "furnishings")
    private String furnishings;
    /**
     * Наличие лифта.
     */
    @Column(name = "elevator")
    private Boolean elevator;
    /**
     * Наличие балкона.
     */
    @Column(name = "balcony")
    private Boolean balcony;
    /**
     * Наличие гаража.
     */
    @Column(name = "garage")
    private Boolean garage;
    /**
     * Наличие двора.
     */
    @Column(name = "courtyard")
    private Boolean courtyard;
    /**
     * Наличие бассейна.
     */
    @Column(name = "pool")
    private Boolean pool;
    /**
     * Наличие кладовой.
     */
    @Column(name = "storeroom")
    private Boolean storeroom;
    /**
     * Номер этажа.
     */
    @Column(name = "floor")
    private Integer floor;
    /**
     * Указывает на наличие кондиционера.
     */
    @Column(name = "air_conditioner")
    private Boolean airConditioner;
}
