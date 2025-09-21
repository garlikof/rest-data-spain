package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Представляет пользовательский профиль с персональными данными.
 */
@Data
@Entity
@Table(name = "user_profile")
public class UserProfile {
    /**
     * Первичный идентификатор профиля.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    /**
     * Пользователь, связанный с профилем.
     */
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    /**
     * Дата рождения пользователя.
     */
    @Column(name = "birthday")
    private java.sql.Date birthday;
    /**
     * Предпочитаемый язык пользователя.
     */
    @Column(name = "language")
    private String language;
    /**
     * Информация о гражданстве.
     */
    @Column(name = "citizenship")
    private String citizenship;
    /**
     * Семейное положение (в схеме используется поле "martial_status").
     */
    @Column(name = "martial_status")
    private String martialStatus;
    /**
     * Отображаемое имя пользователя.
     */
    @Column(name = "name")
    private String name;
}
