package com.example.backend.service;

import com.example.backend.entity.GamePlayer;
import com.example.backend.entity.User;
import java.util.List;

public interface GamePlayerService {
    List<GamePlayer> findAll();
    List<GamePlayer> findByStatus(String status);
    List<GamePlayer> findByGameName(String gameName);
    List<GamePlayer> findByRank(String rank);
    List<GamePlayer> findByRole(String role);
    List<GamePlayer> findByServer(String server);
    GamePlayer findById(Long id);
    GamePlayer save(GamePlayer gamePlayer);
    void deleteById(Long id);
    GamePlayer hirePlayer(Long id, User user, Integer hours);
    GamePlayer returnPlayer(Long id);
    GamePlayer updateRating(Long id, Double rating);
} 