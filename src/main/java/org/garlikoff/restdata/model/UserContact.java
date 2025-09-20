package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "user_contact")
public class UserContact {
    @Id
    @GeneratedValue
    @Column(name = "id") private UUID id;
    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "id")
    private User user;
    @Column(name = "type") private String type;
    @Column(name = "value") private String value;
}
