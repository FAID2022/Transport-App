package com.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    // C'est vide ? OUI !
    // JpaRepository nous donne déjà tout : save(), findAll(), findById(), etc.

    // On peut ajouter une méthode magique si on veut chercher par email plus tard :
    boolean existsByEmail(String email);

    Parent findByNom(String nom);
}