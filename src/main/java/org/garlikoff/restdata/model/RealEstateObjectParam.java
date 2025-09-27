package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
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
     * Объект недвижимости, с которым связаны параметры.
     */
    @ManyToOne
    @JoinColumn(name = "real_estate_object_id")
    private RealEstateObject realEstateObject;
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
    @ManyToOne
    @JoinColumn(name = "type_of_accommodation_id")
    private TypeOfAccommodation typeOfAccommodation;
    /**
     * Сведения о меблировке.
     */
    @Column(name = "furnishings")
    private String furnishings;
    /**
     * Описание объекта недвижимости.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
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
     * Общее количество комнат.
     */
    @Column(name = "number_of_rooms")
    private Integer numberOfRooms;
    /**
     * Стоимость аренды.
     */
    @Column(name = "price")
    private BigDecimal price;
    /**
     * Валюта стоимости аренды.
     */
    @Column(name = "currency")
    private String currency;
    /**
     * Размер депозита.
     */
    @Column(name = "deposit")
    private BigDecimal deposit;
    /**
     * Дата, с которой доступен объект.
     */
    @Column(name = "available_from")
    private LocalDate availableFrom;
    /**
     * Разрешены ли дети.
     */
    @Column(name = "children_allowed")
    private Boolean childrenAllowed;
    /**
     * Разрешены ли домашние животные.
     */
    @Column(name = "pets_allowed")
    private Boolean petsAllowed;
    /**
     * Указывает на наличие кондиционера.
     */
    @Column(name = "air_conditioner")
    private Boolean airConditioner;
}
