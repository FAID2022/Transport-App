package com.app;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bus")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Autorise le site web à lire les données
public class BusController {

    private final BusService monitoringService;

    // --- C'EST ICI LA CORRECTION ---
    // On ne demande plus juste la Latitude (getLastLat), on demande TOUT l'état du bus
    // (Destination, Temps restant, Distance, etc.)
    @GetMapping("/live")
    public ResponseEntity<BusService.BusState> getBusPosition() {
        return ResponseEntity.ok(monitoringService.getLatestBusState());
    }

    // --- GESTION DES PARENTS (Inchangé) ---

    @GetMapping("/parents")
    public List<Parent> getAllParents() {
        return monitoringService.getAllParents();
    }

    @PostMapping("/parents")
    public Parent addParent(@RequestBody Parent parent) {
        return monitoringService.saveParent(parent);
    }

    @DeleteMapping("/parents/{id}")
    public void deleteParent(@PathVariable Long id) {
        monitoringService.deleteParent(id);
    }
}