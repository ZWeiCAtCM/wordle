package com.example.wordle.server;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private GameService svc;

    @PostMapping
    public Map<String, UUID> newGame() throws IOException {
        UUID id = svc.createGame(6, "words.txt");
        return Map.of("gameId", id);
    }

    @PostMapping("/{id}/guesses")
    public GuessResponse guess(@PathVariable UUID id,
                               @RequestBody Map<String,String> req) {
        return svc.submitGuess(id, req.get("guess"));
    }

    @GetMapping("/{id}")
    public GameState state(@PathVariable UUID id) {
        return svc.getState(id);
    }
}
