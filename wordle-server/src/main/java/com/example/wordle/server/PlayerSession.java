package com.example.wordle.server;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import com.example.wordle.CheatingWordleGame;
import com.example.wordle.WordleGame;

/**
 * holds one player's id and its own WordleGame instance
 */
public class PlayerSession {
    private final UUID playerId;
    private final WordleGame gameInstance;

    /**
     * create a new session for a player, duplicating the prototype game
     *
     * @param playerId   the unique id of this player
     * @param prototype  the game prototype (normal or cheating)
     * @param wordFile   the word list resource name
     */
    public PlayerSession(UUID playerId, WordleGame prototype, String wordFile) throws IOException {
        this.playerId = playerId;
        Objects.requireNonNull(prototype, "src must not be null");
        if (prototype instanceof CheatingWordleGame) {
            // cheating mode: each session gets its own CheatingWordleGame
            this.gameInstance = new CheatingWordleGame(prototype.getMaxTurns(), wordFile);
        } else {
            // normal mode: each session gets its own WordleGame
            this.gameInstance = new WordleGame(prototype.getMaxTurns(), wordFile);
        }
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public WordleGame getGameInstance() {
        return gameInstance;
    }
}
