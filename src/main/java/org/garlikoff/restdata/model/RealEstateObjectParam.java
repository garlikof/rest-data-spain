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
     * Тип балкона/террасы (балкон, терраса или отсутствие).
     */
    @Column(name = "balcony_terrace")
    private String balconyTerrace;
    /**
     * Наличие и тип парковки (гараж, парковка или отсутствие).
     */
    @Column(name = "garage_parking")
    private String garageParking;
    /**
     * Тип придомовой территории (сад, двор или отсутствие).
     */
    @Column(name = "garden_yard")
    private String gardenYard;
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
    @Column(name = "housing_condition")
    private String housingCondition;
    /**
     * Номер этажа с учётом классификации (например, Planta baja, Ático).
     */
    @Column(name = "floor")
    private String floor;
    /**
     * Указывает на наличие кондиционера.
     */
    @Column(name = "air_conditioner")
    private Boolean airConditioner;
    /**
     * Тип отопления (газовое, электрическое или отсутствие).
     */
    @Column(name = "heating")
    private String heating;
    /**
     * Энергетический сертификат.
     */
    @Column(name = "energy_certificate")
    private String energyCertificate;
    /**
     * Год постройки дома.
     */
    @Column(name = "year_built")
    private Integer yearBuilt;
    /**
     * Ориентация квартиры (север, юг, запад, восток).
     */
    @Column(name = "orientation")
    private String orientation;
}
