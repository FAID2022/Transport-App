package com.app;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional; // <--- IMPORTE ÇA
@Service
@RequiredArgsConstructor
public class BusService {

    private final ParentRepository parentRepository;
    private final PenaltyRepository penaltyRepository;

    // Pour stocker l'état actuel et l'envoyer au Frontend
    @Getter @Setter
    public static class BusState {
        private double latitude;
        private double longitude;
        private String status;
        private String destination;
        private String distanceRestante;
        private String tempsRestant;
    }

    private BusState latestState = new BusState();

    // Pour éviter de créer 50 pénalités pour le même arrêt
    private String dernierArretTraite = "";

    public void traiterPositionBus(String busId, double lat, double lon, String status, String dest, String dist, String temps) {

        // 1. Mise à jour pour le site web
        latestState.setLatitude(lat);
        latestState.setLongitude(lon);
        latestState.setStatus(status);
        latestState.setDestination(dest);
        latestState.setDistanceRestante(dist);
        latestState.setTempsRestant(temps);

        // 2. LOGIQUE MÉTIER : Vérification des règles
        if ("STOPPED".equalsIgnoreCase(status)) {
            verifierSanction(dest, temps);
        } else {
            // Si le bus bouge, on réinitialise la mémoire du dernier arrêt traité
            dernierArretTraite = "";
        }
    }

    private void verifierSanction(String nomFamille, String tempsStr) {
        try {
            // Le bus envoie "8 min" -> On extrait 8
            String chiffres = tempsStr.replaceAll("[^0-9]", "");
            if (chiffres.isEmpty()) return;

            int dureeReelle = Integer.parseInt(chiffres);

            // RÈGLE : Si > 5 minutes, c'est une infraction
            if (dureeReelle > 5) {

                // Petite sécurité pour ne pas spammer la base de données
                // On n'enregistre la pénalité que si on ne l'a pas déjà fait pour cet arrêt précis
                // (Astuce simple : on combine le nom + la durée pour voir si ça change)
                String cleUniqueArret = nomFamille + "-" + dureeReelle;

                if (!cleUniqueArret.equals(dernierArretTraite)) {

                    Parent p = trouverParentParNom(nomFamille);
                    if (p != null) {
                        System.out.println("⚖️ JUGEMENT : Retard confirmé chez " + nomFamille + " (" + dureeReelle + " min). Pénalité !");

                        // Création de l'amende
                        Penalty pen = new Penalty();
                        pen.setParent(p);
                        pen.setDateInfraction(LocalDateTime.now());
                        pen.setDureeTotale(dureeReelle);
                        pen.setTempsRetard(dureeReelle - 5);
                        penaltyRepository.save(pen);

                        // Mise à jour compteur parent
                        p.setNombrePenalites(p.getNombrePenalites() + 1);
                        parentRepository.save(p);

                        // On note qu'on a traité ce cas
                        dernierArretTraite = cleUniqueArret;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur analyse pénalité : " + e.getMessage());
        }
    }

    private Parent trouverParentParNom(String nom) {
        List<Parent> parents = parentRepository.findAll();
        for (Parent p : parents) {
            if (p.getNom().equalsIgnoreCase(nom)) {
                return p;
            }
        }
        return null;
    }

    public BusState getLatestBusState() {
        return latestState;
    }

    // Méthodes CRUD Parents
    public List<Parent> getAllParents() { return parentRepository.findAll(); }
    public Parent saveParent(Parent p) { return parentRepository.save(p); }
    @Transactional // <--- Obligatoire pour faire une suppression "deleteBy..."
    public void deleteParent(Long id) {
        // 1. D'abord, on supprime l'historique des pénalités de cette famille
        penaltyRepository.deleteByParentId(id);

        // 2. Ensuite, on peut supprimer la famille sans que MySQL ne se plaigne
        parentRepository.deleteById(id);

        System.out.println("🗑️ Famille et pénalités supprimées pour l'ID : " + id);
    }
}