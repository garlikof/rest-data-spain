package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Представляет заявку на аренду, созданную пользователем.
 */
@Data
@Entity
@Table(name = "rental_claim")
public class RentalClaim {
    /**
     * Первичный идентификатор заявки.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Пользователь, создавший заявку.
     */
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    /**
     * Указывает, ожидается ли проживание с детьми.
     */
    @Column(name = "childs")
    private Boolean childs;
    /**
     * Указывает, предполагается ли проживание с питомцами.
     */
    @Column(name = "pets")
    private Boolean pets;
    /**
     * Желаемый ценовой диапазон (int4range).
     */
    @Column(name = "price")
    private String price; // int4range
    /**
     * Запрашиваемые идентификаторы местоположений.
     */
    @Column(name = "location_ids")
    private UUID[] locationIds;
    /**
     * Количество жильцов.
     */
    @Column(name = "number_of_residents")
    private Integer numberOfResidents;
    /**
     * Требуемое количество комнат.
     */
    @Column(name = "number_of_rooms")
    private Short[] numberOfRooms;
    /**
     * Желаемая дата начала аренды.
     */
    @Column(name = "date_begin")
    private java.sql.Date dateBegin;
}
