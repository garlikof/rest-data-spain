package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

/**
 * Represents an application user in the system.
 */
@Data
@Entity
@Table(name = "user")
public class User {
    /**
     * Primary identifier of the user.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Flag indicating whether the user acts as an agency.
     */
    @Column(name = "is_agency")
    private Boolean isAgency;
    /**
     * Name of the user.
     */
    @Column(name = "name")
    private String name;
}
