package com.receipt2recipe.r2r.repository;

import com.receipt2recipe.r2r.domain.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FridgeRepository extends JpaRepository<Fridge, Long> {
    Optional<Fridge> findByMemberUserEmail(String userEmail);
}
