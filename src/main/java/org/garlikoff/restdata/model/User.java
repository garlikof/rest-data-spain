package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

/**
 * Представляет пользователя приложения в системе.
 */
@Data
@Entity
@Table(name = "user")
public class User {
    /**
     * Первичный идентификатор пользователя.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Флаг, указывающий, выступает ли пользователь как агентство.
     */
    @Column(name = "is_agency")
    private Boolean isAgency;
    /**
     * Имя пользователя.
     */
    @Column(name = "name")
    private String name;
}
