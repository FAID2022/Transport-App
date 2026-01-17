package com.app;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/penalties")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Important pour le HTML
public class PenaltyController {

    private final PenaltyRepository penaltyRepository;

    // Le site va appeler cette URL pour voir les amendes
    @GetMapping
    public List<Penalty> getPenalties() {
        return penaltyRepository.findAll(); // Récupère tout l'historique
    }
}