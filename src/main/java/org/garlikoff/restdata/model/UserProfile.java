package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Column(name = "birthday") private java.sql.Date birthday;
    @Column(name = "language") private String language;
    @Column(name = "citizenship") private String citizenship;
    @Column(name = "martial_status") private String martialStatus;
    @Column(name = "name") private String name;
}