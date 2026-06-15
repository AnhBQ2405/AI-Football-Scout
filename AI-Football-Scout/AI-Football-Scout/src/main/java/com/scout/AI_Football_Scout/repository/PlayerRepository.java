package com.scout.AI_Football_Scout.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scout.AI_Football_Scout.entity.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findFirstByNameContainingIgnoreCase(String name);
    List<Player> findByPositionIgnoreCase(String position);
}