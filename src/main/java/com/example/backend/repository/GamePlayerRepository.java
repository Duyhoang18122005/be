package com.example.backend.repository;

import com.example.backend.entity.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    List<GamePlayer> findByStatus(String status);
    List<GamePlayer> findByHiredBy_Id(Long userId);
    List<GamePlayer> findByGameNameAndStatus(String gameName, String status);
    List<GamePlayer> findByRankAndStatus(String rank, String status);
    List<GamePlayer> findByRoleAndStatus(String role, String status);
    List<GamePlayer> findByServerAndStatus(String server, String status);
    List<GamePlayer> findByGameNameAndRankAndStatus(String gameName, String rank, String status);
} 