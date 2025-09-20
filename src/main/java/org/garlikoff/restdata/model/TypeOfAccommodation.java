package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "type_of_accommodation")
public class TypeOfAccommodation {
    @Id
    @GeneratedValue
    @Column(name = "id") private UUID id;
    @Column(name = "name") private String name;
}