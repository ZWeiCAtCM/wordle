package com.example.wordle.server;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public Map<String, UUID> newGame() throws IOException {
        UUID id = svc.createGame();
        return Map.of("gameId", id);
    }

    @PostMapping("/{id}/guesses")
    public ResponseEntity<?> guess(
        @PathVariable UUID id,
        @RequestBody Map<String, String> req
    ) {
        try {
            GuessResponse resp = svc.submitGuess(id, req.get("guess"));
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public GameState state(@PathVariable UUID id) {
        return svc.getState(id);
    }
}
