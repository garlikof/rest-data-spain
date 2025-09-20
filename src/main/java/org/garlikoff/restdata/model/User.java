package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "id") private UUID id;
    @Column(name = "is_agency") private Boolean isAgency;
    @Column(name = "name") private String name;
}
