package com.example.wordle.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private final Map<UUID, List<PlayerSession>> sessions = new ConcurrentHashMap<>();

    /**
     * @param prototype the game prototype (normal or cheating)
     * @param wordFile the word list resource name on classpath
     */
    public GameService(
        WordleGame prototype,
        @Value("${wordle.wordFile}") String wordFile
    ) {
        this.prototype = prototype;
        this.wordFile = wordFile;
    }

    /**
     * create a new game and return its id
     */
    public UUID createGame() throws IOException {
        WordleGame game = duplicate(prototype);
        UUID gameId = UUID.randomUUID();
        games.put(gameId, game);
        System.out.println("List of words remaining: " + game.getWordList());
        return gameId;
    }

    /**
     * player joins an existing game, returns playerId
     */
    public UUID joinGame(UUID gameId) throws IOException {
        WordleGame game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("game not found");
        }
        System.out.println("List of words remaining: " + game.getWordList());
        UUID playerId = UUID.randomUUID();
        PlayerSession ps = new PlayerSession(playerId, prototype, wordFile);
        sessions.computeIfAbsent(gameId, k -> new ArrayList<>()).add(ps);
        return playerId;
    }

    /**
     * submit a guess on behalf of a specific player
     */
    public GuessResponse submitGuess(UUID gameId, UUID playerId, String guess) {
        List<PlayerSession> list = sessions.get(gameId);
        if (list == null) {
            throw new IllegalArgumentException("game not joined");
        }
        PlayerSession ps = list.stream()
            .filter(s -> s.getPlayerId().equals(playerId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("player not joined"));

        WordleGame game = ps.getGameInstance();
        boolean won = game.guess(guess);
        System.out.println("List of words remaining: " + game.getWordList());
        // return marks/result for this player
        return new GuessResponse(
            game.getLastMarks(),
            won,
            game.isOver(),
            game.getTurnsUsed(),
            game.getMaxTurns(),
            game.isOver() ? game.getAnswer() : null
        );
    }

    /**
     * get progress of all players in this game
     */
    public List<GameState> getProgress(UUID gameId) {
        List<PlayerSession> list = sessions.get(gameId);
        if (list == null) {
            throw new IllegalArgumentException("game not joined");
        }
        List<GameState> result = new ArrayList<>();
        for (PlayerSession ps : list) {
            WordleGame g = ps.getGameInstance();
            result.add(new GameState(
                ps.getPlayerId(),
                g.getTurnsUsed(),
                g.getMaxTurns(),
                g.hasWon(),
                g.isOver(),
                g.isOver() ? g.getAnswer() : null
            ));
        }
        return result;
    }

    /**
     * get single-player game state (legacy support)
     */
    public GameState getState(UUID gameId) {
        WordleGame game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("game not found");
        }
        String answer = game.isOver() ? game.getAnswer() : null;
        return new GameState(
            gameId,
            game.getTurnsUsed(),
            game.getMaxTurns(),
            game.hasWon(),
            game.isOver(),
            answer
        );
    }

    /**
     * duplicate prototype into a fresh game instance
     */
    private WordleGame duplicate(WordleGame src) throws IOException {
        Objects.requireNonNull(src, "src must not be null");
        if (src instanceof CheatingWordleGame) {
            return new CheatingWordleGame(src.getMaxTurns(), wordFile);
        }
        return new WordleGame(src.getMaxTurns(), wordFile);
    }
}
