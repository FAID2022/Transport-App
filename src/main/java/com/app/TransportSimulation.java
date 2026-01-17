package com.app;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

public class TransportSimulation {

    static class PointGPS {
        double lat, lon;
        String nom;
        public PointGPS(double lat, double lon, String nom) { this.lat = lat; this.lon = lon; this.nom = nom; }
    }

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        Random random = new Random();
        String busId = "BUS-SCHOOL-01";

        // ✅ CORRECTION ICI : Les nouvelles coordonnées de l'école (EMI)
        PointGPS ecole = new PointGPS(33.9981, -6.7943, "L'École");

        // Position de départ
        double currentLat = ecole.lat;
        double currentLon = ecole.lon;

        System.out.println("🚌 DÉPART DU BUS (Coordonnées : " + ecole.lat + ", " + ecole.lon + ")");

        // On récupère le trajet
        List<PointGPS> circuit = getParentsFromDB();

        // ✅ C'est cette ligne qui assure le RETOUR À L'ÉCOLE à la fin
        // Après avoir fait tous les parents, il ajoute l'école comme dernière destination.
        circuit.add(ecole);

        if (circuit.isEmpty()) {
            System.out.println("⚠️ Aucun parent en BDD.");
            return;
        }

        // Boucle du trajet
        for (PointGPS destination : circuit) {
            System.out.println("\n🚀 Direction -> " + destination.nom);

            // --- PHASE 1 : DÉPLACEMENT ---
            int steps = 20;
            double stepLat = (destination.lat - currentLat) / steps;
            double stepLon = (destination.lon - currentLon) / steps;

            for (int i = 0; i < steps; i++) {
                currentLat += stepLat;
                currentLon += stepLon;

                // Calcul distance pour affichage
                double dist = Math.sqrt(Math.pow(destination.lat - currentLat, 2) + Math.pow(destination.lon - currentLon, 2)) * 111;
                String distStr = String.format("%.2f km", dist);

                String json = String.format(Locale.US,
                        "{\"id\":\"%s\", \"lat\":%.5f, \"lon\":%.5f, \"status\":\"MOVING\", \"destination\":\"%s\", \"distance\":\"%s\", \"temps\":\"En route\"}",
                        busId, currentLat, currentLon, destination.nom, distStr);

                producer.send(new ProducerRecord<>("bus-positions", busId, json));
                Thread.sleep(500);
            }

            // --- PHASE 2 : ARRÊT ---
            System.out.println("🛑 ARRIVÉ chez " + destination.nom);

            // Si c'est l'école (la fin de la liste), on s'arrête définitivement
            if (destination.nom.equals("L'École")) {
                String jsonFin = String.format(Locale.US, "{\"id\":\"%s\", \"lat\":%.5f, \"lon\":%.5f, \"status\":\"GARAGE\", \"destination\":\"Terminus (École)\", \"distance\":\"0\", \"temps\":\"0\"}", busId, destination.lat, destination.lon);
                producer.send(new ProducerRecord<>("bus-positions", busId, jsonFin));
                System.out.println("🏁 TERMINUS : Retour à l'école effectué.");
                break;
            }

            // Sinon (arrêt parent), on attend un peu
            int minutesReelles = random.nextInt(9) + 2;
            System.out.println("   ⏳ Arrêt physique de " + minutesReelles + " min...");

            for (int t = 0; t < minutesReelles; t++) {
                String jsonStop = String.format(Locale.US,
                        "{\"id\":\"%s\", \"lat\":%.5f, \"lon\":%.5f, \"status\":\"STOPPED\", \"destination\":\"%s\", \"distance\":\"0 km\", \"temps\":\"%d min\"}",
                        busId, destination.lat, destination.lon, destination.nom, minutesReelles);

                producer.send(new ProducerRecord<>("bus-positions", busId, jsonStop));
                Thread.sleep(1000);
            }
        }
        producer.close();
    }

    private static List<PointGPS> getParentsFromDB() {
        List<PointGPS> parents = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/transport_scolaire", "root", "1234");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nom, latitude, longitude FROM parents")) {
            while (rs.next()) {
                parents.add(new PointGPS(rs.getDouble("latitude"), rs.getDouble("longitude"), rs.getString("nom")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return parents;
    }
}