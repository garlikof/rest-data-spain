package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Represents a user profile with personal information.
 */
@Data
@Entity
@Table(name = "user_profile")
public class UserProfile {
    /**
     * Primary identifier of the profile.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    /**
     * Associated user for the profile.
     */
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    /**
     * Birthdate of the user.
     */
    @Column(name = "birthday")
    private java.sql.Date birthday;
    /**
     * Preferred language of the user.
     */
    @Column(name = "language")
    private String language;
    /**
     * Citizenship information.
     */
    @Column(name = "citizenship")
    private String citizenship;
    /**
     * Marital status (note: schema uses "martial_status").
     */
    @Column(name = "martial_status")
    private String martialStatus;
    /**
     * Display name of the user.
     */
    @Column(name = "name")
    private String name;
}
