package com.app;

import org.springframework.data.jpa.repository.JpaRepository; // <-- IMPORTANT
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    // C'est cette partie qui donne la méthode .save() !

    List<Penalty> findByParentId(Long parentId);

    void deleteByParentId(Long parentId);
}