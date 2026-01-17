package com.app;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "parents")
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private double latitude;
    private double longitude;
    private String email;

    // NOUVEAU CHAMP : Compteur de pénalités
    // On l'initialise à 0 par défaut
    private int nombrePenalites = 0;
}