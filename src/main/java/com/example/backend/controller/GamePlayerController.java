package com.example.backend.controller;

import com.example.backend.entity.GamePlayer;
import com.example.backend.entity.User;
import com.example.backend.service.GamePlayerService;
import com.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game-players")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Game Player", description = "Game player management APIs")
public class GamePlayerController {

    private final GamePlayerService gamePlayerService;
    private final UserService userService;

    public GamePlayerController(GamePlayerService gamePlayerService, UserService userService) {
        this.gamePlayerService = gamePlayerService;
        this.userService = userService;
    }

    @Operation(summary = "Get all game players")
    @GetMapping
    public ResponseEntity<List<GamePlayer>> getAllGamePlayers() {
        return ResponseEntity.ok(gamePlayerService.findAll());
    }

    @Operation(summary = "Get available game players")
    @GetMapping("/available")
    public ResponseEntity<List<GamePlayer>> getAvailableGamePlayers() {
        return ResponseEntity.ok(gamePlayerService.findByStatus("AVAILABLE"));
    }

    @Operation(summary = "Get game players by game name")
    @GetMapping("/game/{gameName}")
    public ResponseEntity<List<GamePlayer>> getGamePlayersByGame(
            @Parameter(description = "Game name") @PathVariable String gameName) {
        return ResponseEntity.ok(gamePlayerService.findByGameName(gameName));
    }

    @Operation(summary = "Get game players by rank")
    @GetMapping("/rank/{rank}")
    public ResponseEntity<List<GamePlayer>> getGamePlayersByRank(
            @Parameter(description = "Player rank") @PathVariable String rank) {
        return ResponseEntity.ok(gamePlayerService.findByRank(rank));
    }

    @Operation(summary = "Get game players by role")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<GamePlayer>> getGamePlayersByRole(
            @Parameter(description = "Player role") @PathVariable String role) {
        return ResponseEntity.ok(gamePlayerService.findByRole(role));
    }

    @Operation(summary = "Get game players by server")
    @GetMapping("/server/{server}")
    public ResponseEntity<List<GamePlayer>> getGamePlayersByServer(
            @Parameter(description = "Game server") @PathVariable String server) {
        return ResponseEntity.ok(gamePlayerService.findByServer(server));
    }

    @Operation(summary = "Get game player by ID")
    @GetMapping("/{id}")
    public ResponseEntity<GamePlayer> getGamePlayer(
            @Parameter(description = "Game player ID") @PathVariable Long id) {
        return ResponseEntity.ok(gamePlayerService.findById(id));
    }

    @Operation(summary = "Create new game player")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GamePlayer> createGamePlayer(
            @Valid @RequestBody GamePlayerRequest request) {
        GamePlayer gamePlayer = new GamePlayer();
        updateGamePlayerFromRequest(gamePlayer, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gamePlayerService.save(gamePlayer));
    }

    @Operation(summary = "Update game player")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GamePlayer> updateGamePlayer(
            @Parameter(description = "Game player ID") @PathVariable Long id,
            @Valid @RequestBody GamePlayerRequest request) {
        GamePlayer gamePlayer = gamePlayerService.findById(id);
        updateGamePlayerFromRequest(gamePlayer, request);
        return ResponseEntity.ok(gamePlayerService.save(gamePlayer));
    }

    @Operation(summary = "Delete game player")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteGamePlayer(
            @Parameter(description = "Game player ID") @PathVariable Long id) {
        gamePlayerService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Hire a game player")
    @PostMapping("/{id}/hire")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GamePlayer> hireGamePlayer(
            @Parameter(description = "Game player ID") @PathVariable Long id,
            @Valid @RequestBody HireRequest hireRequest,
            Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        return ResponseEntity.ok(gamePlayerService.hirePlayer(id, user, hireRequest.getHours()));
    }

    @Operation(summary = "Return a hired game player")
    @PostMapping("/{id}/return")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GamePlayer> returnGamePlayer(
            @Parameter(description = "Game player ID") @PathVariable Long id) {
        return ResponseEntity.ok(gamePlayerService.returnPlayer(id));
    }

    @Operation(summary = "Rate a game player")
    @PostMapping("/{id}/rate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GamePlayer> rateGamePlayer(
            @Parameter(description = "Game player ID") @PathVariable Long id,
            @Valid @RequestBody RatingRequest ratingRequest) {
        return ResponseEntity.ok(gamePlayerService.updateRating(id, ratingRequest.getRating()));
    }

    private void updateGamePlayerFromRequest(GamePlayer gamePlayer, GamePlayerRequest request) {
        gamePlayer.setUsername(request.getUsername());
        gamePlayer.setGameName(request.getGameName());
        gamePlayer.setRank(request.getRank());
        gamePlayer.setRole(request.getRole());
        gamePlayer.setServer(request.getServer());
        gamePlayer.setPricePerHour(request.getPricePerHour());
        gamePlayer.setDescription(request.getDescription());
        gamePlayer.setStatus(request.getStatus());
    }
}

@Data
class GamePlayerRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Game name is required")
    private String gameName;

    @NotBlank(message = "Rank is required")
    private String rank;

    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "Server is required")
    private String server;

    @NotNull(message = "Price per hour is required")
    private java.math.BigDecimal pricePerHour;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotBlank(message = "Status is required")
    private String status;
}

@Data
class HireRequest {
    @NotNull(message = "Hours is required")
    @Min(value = 1, message = "Hours must be at least 1")
    private Integer hours;
}

@Data
class RatingRequest {
    @NotNull(message = "Rating is required")
    @Min(value = 0, message = "Rating must be greater than or equal to 0")
    @Max(value = 5, message = "Rating must be less than or equal to 5")
    private Double rating;
} 