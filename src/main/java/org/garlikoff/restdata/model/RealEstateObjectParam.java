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
     * Тип жилья (квартиры, дома, комнаты).
     */
    @ManyToOne
    @JoinColumn(name = "type")
    private Word type;
    /**
     * Сведения о меблировке.
     */
    @ManyToOne
    @JoinColumn(name = "furnishings")
    private Word furnishings;
    /**
     * Наличие лифта.
     */
    @Column(name = "elevator")
    private Boolean elevator;
    /**
     * Тип балкона/террасы (балкон, терраса или отсутствие).
     */
    @ManyToOne
    @JoinColumn(name = "balcony_terrace")
    private Word balconyTerrace;
    /**
     * Наличие и тип парковки (гараж, парковка или отсутствие).
     */
    @ManyToOne
    @JoinColumn(name = "garage_parking")
    private Word garageParking;
    /**
     * Тип придомовой территории (сад, двор или отсутствие).
     */
    @ManyToOne
    @JoinColumn(name = "garden_yard")
    private Word gardenYard;
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
     * Состояние жилья.
     */
    @ManyToOne
    @JoinColumn(name = "housing_condition")
    private Word housingCondition;
    /**
     * Номер этажа с учётом классификации (например, Planta baja, Ático).
     */
    @ManyToOne
    @JoinColumn(name = "floor")
    private Word floor;
    /**
     * Указывает на наличие кондиционера.
     */
    @Column(name = "air_conditioner")
    private Boolean airConditioner;
    /**
     * Тип отопления (газовое, электрическое или отсутствие).
     */
    @ManyToOne
    @JoinColumn(name = "heating")
    private Word heating;
    /**
     * Энергетический сертификат.
     */
    @ManyToOne
    @JoinColumn(name = "energy_certificate")
    private Word energyCertificate;
    /**
     * Год постройки дома.
     */
    @Column(name = "year_built")
    private Integer yearBuilt;
    /**
     * Ориентация квартиры (север, юг, запад, восток).
     */
    @ManyToOne
    @JoinColumn(name = "orientation")
    private Word orientation;
}
