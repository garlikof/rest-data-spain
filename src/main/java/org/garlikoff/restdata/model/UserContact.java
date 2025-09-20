package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Represents a contact detail for a user.
 */
@Data
@Entity
@Table(name = "user_contact")
public class UserContact {
    /**
     * Primary identifier for the contact.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Associated user.
     */
    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "id")
    private User user;
    /**
     * Contact type, e.g., phone or email.
     */
    @Column(name = "type")
    private String type;
    /**
     * Contact value.
     */
    @Column(name = "value")
    private String value;
}
