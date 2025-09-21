package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;
import org.postgresql.geometric.PGpoint;

import java.util.UUID;

@Data
@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue
    @Column(name = "id") private UUID id;
    @Column(name = "name_key") private String nameKey;
    @Column(name = "type") private String type;
    @ManyToOne
    @JoinColumn(name = "parent_id") private Location parent;
    @Column(name = "center", columnDefinition = "point") private PGpoint center;
}
