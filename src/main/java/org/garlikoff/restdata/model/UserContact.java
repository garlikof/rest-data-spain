package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Представляет контактные данные пользователя.
 */
@Data
@Entity
@Table(name = "user_contact")
public class UserContact {
    /**
     * Первичный идентификатор контакта.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Связанный пользователь.
     */
    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "id")
    private User user;
    /**
     * Тип контакта, например телефон или электронная почта.
     */
    @Column(name = "type")
    private String type;
    /**
     * Значение контакта.
     */
    @Column(name = "value")
    private String value;
}
