package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "real_estate_object")
public class RealEstateObject {
    @Id
    @GeneratedValue
    @Column(name = "id") private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id") private User user;
}
