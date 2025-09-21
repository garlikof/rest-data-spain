package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Представляет объект недвижимости, принадлежащий пользователю.
 */
@Data
@Entity
@Table(name = "real_estate_object")
public class RealEstateObject {
    /**
     * Первичный идентификатор объекта недвижимости.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Владелец объекта недвижимости.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
