package com.example.wordle.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.wordle.server.dto.GameState;
import com.example.wordle.server.dto.GuessResponse;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService svc;

    @Autowired
    public GameController(GameService svc) {
        this.svc = svc;
    }

    /**
     * create a new game, returns gameId
     */
    @PostMapping
    public Map<String, UUID> newGame() throws IOException {
        UUID id = svc.createGame();
        return Map.of("gameId", id);
    }

    /**
     * player joins an existing game, returns playerId
     */
    @PostMapping("/{gameId}/join")
    public Map<String, UUID> joinGame(@PathVariable UUID gameId) throws IOException {
        UUID playerId = svc.joinGame(gameId);
        return Map.of("playerId", playerId);
    }

    /**
     * submit a guess for a specific player
     */
    @PostMapping("/{gameId}/guesses")
    public ResponseEntity<?> guess(
        @PathVariable UUID gameId,
        @RequestHeader("X-Player-Id") UUID playerId,
        @RequestBody Map<String, String> req
    ) {
        try {
            GuessResponse resp = svc.submitGuess(gameId, playerId, req.get("guess"));
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * get overall progress for all players
     */
    @GetMapping("/{gameId}/progress")
    public List<GameState> progress(@PathVariable UUID gameId) {
        return svc.getProgress(gameId);
    }

    /**
     * legacy single-player state endpoint
     */
    @GetMapping("/{gameId}")
    public GameState state(@PathVariable UUID gameId) {
        return svc.getState(gameId);
    }
}
