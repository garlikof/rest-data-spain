package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Представляет предложение об аренде объекта недвижимости.
 */
@Data
@Entity
@Table(name = "rental_proposal")
public class RentalProposal {
    /**
     * Первичный идентификатор предложения.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Объект недвижимости, связанный с предложением.
     */
    @ManyToOne
    @JoinColumn(name = "real_estate_object_id")
    private RealEstateObject realEstateObject;
    /**
     * Пользователь, создавший предложение.
     */
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    /**
     * Местоположение, связанное с предложением.
     */
    @ManyToOne
    @JoinColumn(name = "location")
    private Location location;
    /**
     * Цена предложения.
     */
    @Column(name = "price")
    private Double price;
    /**
     * Тип предложения.
     */
    @Column(name = "type")
    private String type;
    /**
     * Указывает, разрешены ли домашние животные.
     */
    @Column(name = "pets")
    private Boolean pets;
    /**
     * Дата создания предложения.
     */
    @Column(name = "proposal_created")
    private java.sql.Date proposalCreated;
    /**
     * Статус предложения.
     */
    @Column(name = "proposal_status")
    private String proposalStatus;
}
