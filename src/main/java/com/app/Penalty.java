package com.app;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "penalties")
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien vers la table Parent (Clé étrangère)
    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    private LocalDateTime dateInfraction;

    private int dureeTotale; // Temps total d'arrêt (ex: 8 min)
    private int tempsRetard; // Temps facturé (ex: 3 min)
}