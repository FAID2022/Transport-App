package com.app;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // <--- TRES IMPORTANT : Autorise le HTML à envoyer des données
public class AdController {

    private final ParentRepository parentRepository;

    @PostMapping("/parents")
    public Parent ajouterParent(@RequestBody Parent parent) {
        return parentRepository.save(parent);
    }

    @GetMapping("/parents")
    public List<Parent> listerParents() {
        return parentRepository.findAll();
    }

    @DeleteMapping("/parents/{id}") // L'URL sera /api/admin/parents/1, /api/admin/parents/2, etc.
    public void supprimerParent(@PathVariable Long id) {
        parentRepository.deleteById(id);
        System.out.println("🗑️ Parent supprimé : ID " + id);
    }
}