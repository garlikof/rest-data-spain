package org.garlikoff.restdata.model;

import jakarta.persistence.*;
import lombok.Data;
import org.postgresql.geometric.PGpoint;

import java.util.UUID;

/**
 * Представляет географическое местоположение.
 */
@Data
@Entity
@Table(name = "location")
public class Location {
    /**
     * Первичный идентификатор местоположения.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    /**
     * Ключ ссылки на перевод названия местоположения.
     */
    @Column(name = "name_key")
    private String nameKey;
    /**
     * Тип местоположения.
     */
    @Column(name = "type")
    private String type;
    /**
     * Родительское местоположение, если оно существует.
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Location parent;
  
    @Column(name = "center", columnDefinition = "point")
    private PGpoint center;

    /**
     * Ссылка на местоположение в Google Maps.
     */
    @Column(name = "url")
    private String url;
}
