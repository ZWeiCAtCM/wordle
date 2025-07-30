package com.example.wordle.server;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.wordle.WordleGame;
import com.example.wordle.server.dto.GameState;
import com.example.wordle.server.dto.GuessResponse;

@Service
public class GameService {
    private final Map<UUID, WordleGame> games = new ConcurrentHashMap<>();

    public UUID createGame(int maxRounds, String wordFile) throws IOException {
        WordleGame game = new WordleGame(maxRounds, wordFile);
        UUID id = UUID.randomUUID();
        games.put(id, game);
        return id;
    }

    public GuessResponse submitGuess(UUID id, String guess) {
        WordleGame game = games.get(id);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        boolean won = game.guess(guess);
        return new GuessResponse(
            game.getLastMarks(),
            won,
            game.isOver(),
            game.getTurnsUsed()
        );
    }

    public GameState getState(UUID id) {
        WordleGame game = games.get(id);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new GameState(
            id,
            game.getTurnsUsed(),
            game.hasWon(),
            game.isOver()
        );
    }
}
