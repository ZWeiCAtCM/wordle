package com.example.wordle.server;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.wordle.CheatingWordleGame;
import com.example.wordle.WordleGame;
import com.example.wordle.server.dto.GameState;
import com.example.wordle.server.dto.GuessResponse;

@Service
public class GameService {

    private final WordleGame prototype;
    private final String wordFile;
    private final Map<UUID, WordleGame> games = new ConcurrentHashMap<>();

    public GameService(
        WordleGame prototype,
        @Value("${wordle.wordFile}") String wordFile) {
        this.prototype = prototype;
        this.wordFile = wordFile;
    }

    /**
     * create a new game instance based on prototype
     */
    public UUID createGame() throws IOException {
        WordleGame game = duplicate(prototype);
        UUID id = UUID.randomUUID();
        games.put(id, game);
        System.out.println("List of words remaining: " + game.getWordList());
        return id;
    }

    public GuessResponse submitGuess(UUID id, String guess) {
        WordleGame game = games.get(id);
        if (game == null) {
            throw new IllegalArgumentException("game not found");
        }
        boolean won = game.guess(guess);
        System.out.println("List of words remaining: " + game.getWordList());
        return new GuessResponse(
                game.getLastMarks(),
                won,
                game.isOver(),
                game.getTurnsUsed(),
                game.getMaxTurns()
        );
    }

    public GameState getState(UUID id) {
        WordleGame game = games.get(id);
        if (game == null) {
            throw new IllegalArgumentException("game not found");
        }
        String answer = game.isOver() ? game.getAnswer() : null;
        return new GameState(
                id,
                game.getTurnsUsed(),
                game.getMaxTurns(),
                game.hasWon(),
                game.isOver(),
                answer
        );
    }

    private WordleGame duplicate(WordleGame src) throws IOException {
        Objects.requireNonNull(src, "src must not be null");
        if (src instanceof CheatingWordleGame cheatSrc) {
            // here 'cheatSrc' is already cast for you
            return new CheatingWordleGame(cheatSrc.getMaxTurns(), wordFile);
        }
        // otherwise it must be a plain WordleGame
        return new WordleGame(src.getMaxTurns(), wordFile);
    }
}
