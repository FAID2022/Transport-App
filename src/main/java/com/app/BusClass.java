package com.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusClass {

    private final BusService monitoringService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "bus-positions", groupId = "school-group")
    public void ecouterBus(String messageJson) {
        try {
            JsonNode json = objectMapper.readTree(messageJson);

            String busId = json.has("id") ? json.get("id").asText() : "Bus-1";
            double lat = json.get("lat").asDouble();
            double lon = json.get("lon").asDouble();
            String status = json.get("status").asText();

            // Extraction des métadonnées (Destination, Temps d'arrêt...)
            String destination = json.has("destination") ? json.get("destination").asText() : "--";
            String distance = json.has("distance") ? json.get("distance").asText() : "--";
            String temps = json.has("temps") ? json.get("temps").asText() : "0 min";

            // On délègue tout au Service "Intelligent"
            monitoringService.traiterPositionBus(busId, lat, lon, status, destination, distance, temps);

        } catch (Exception e) {
            System.err.println("Erreur JSON : " + e.getMessage());
        }
    }
}