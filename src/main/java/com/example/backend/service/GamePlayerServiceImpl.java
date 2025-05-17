package com.example.backend.service;

import com.example.backend.entity.GamePlayer;
import com.example.backend.entity.User;
import com.example.backend.repository.GamePlayerRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Service
@Validated
public class GamePlayerServiceImpl implements GamePlayerService {

    private final GamePlayerRepository gamePlayerRepository;

    public GamePlayerServiceImpl(GamePlayerRepository gamePlayerRepository) {
        this.gamePlayerRepository = gamePlayerRepository;
    }

    @Override
    public List<GamePlayer> findAll() {
        return gamePlayerRepository.findAll();
    }

    @Override
    public List<GamePlayer> findByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        return gamePlayerRepository.findByStatus(status);
    }

    @Override
    public List<GamePlayer> findByGameName(String gameName) {
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be null or empty");
        }
        return gamePlayerRepository.findByGameNameAndStatus(gameName, "AVAILABLE");
    }

    @Override
    public List<GamePlayer> findByRank(String rank) {
        if (rank == null || rank.trim().isEmpty()) {
            throw new IllegalArgumentException("Rank cannot be null or empty");
        }
        return gamePlayerRepository.findByRankAndStatus(rank, "AVAILABLE");
    }

    @Override
    public List<GamePlayer> findByRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        return gamePlayerRepository.findByRoleAndStatus(role, "AVAILABLE");
    }

    @Override
    public List<GamePlayer> findByServer(String server) {
        if (server == null || server.trim().isEmpty()) {
            throw new IllegalArgumentException("Server cannot be null or empty");
        }
        return gamePlayerRepository.findByServerAndStatus(server, "AVAILABLE");
    }

    @Override
    public GamePlayer findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return gamePlayerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game player not found with id: " + id));
    }

    @Override
    @Transactional
    public GamePlayer save(GamePlayer gamePlayer) {
        if (gamePlayer == null) {
            throw new IllegalArgumentException("Game player cannot be null");
        }
        if (gamePlayer.getUser() == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        // Check if user already has a GamePlayer account
        GamePlayer existingPlayer = gamePlayerRepository.findByUser_Id(gamePlayer.getUser().getId());
        if (existingPlayer != null) {
            throw new RuntimeException("User already has a GamePlayer account");
        }
        
        return gamePlayerRepository.save(gamePlayer);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (!gamePlayerRepository.existsById(id)) {
            throw new RuntimeException("Game player not found with id: " + id);
        }
        gamePlayerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public GamePlayer hirePlayer(Long id, User user, Integer hours) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (hours == null || hours < 1) {
            throw new IllegalArgumentException("Hours must be at least 1");
        }

        GamePlayer gamePlayer = findById(id);
        if (!"AVAILABLE".equals(gamePlayer.getStatus())) {
            throw new RuntimeException("Game player is not available for hire");
        }

        gamePlayer.setStatus("HIRED");
        gamePlayer.setHiredBy(user);
        gamePlayer.setHireDate(LocalDate.now());
        gamePlayer.setHoursHired(hours);
        gamePlayer.setReturnDate(LocalDate.now().plusDays(1));

        return gamePlayerRepository.save(gamePlayer);
    }

    @Override
    @Transactional
    public GamePlayer returnPlayer(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        GamePlayer gamePlayer = findById(id);
        if (!"HIRED".equals(gamePlayer.getStatus())) {
            throw new RuntimeException("Game player is not currently hired");
        }

        gamePlayer.setStatus("AVAILABLE");
        gamePlayer.setHiredBy(null);
        gamePlayer.setHireDate(null);
        gamePlayer.setReturnDate(null);
        gamePlayer.setHoursHired(null);

        return gamePlayerRepository.save(gamePlayer);
    }

    @Override
    @Transactional
    public GamePlayer updateRating(Long id, Double rating) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (rating == null) {
            throw new IllegalArgumentException("Rating cannot be null");
        }
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        GamePlayer gamePlayer = findById(id);
        if (gamePlayer.getRating() == null) {
            gamePlayer.setRating(rating);
        } else {
            gamePlayer.setRating((gamePlayer.getRating() + rating) / 2);
        }

        return gamePlayerRepository.save(gamePlayer);
    }
} 