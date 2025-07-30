package com.example.wordle.server.dto;

import java.util.UUID;

/**
 * State of an ongoing game.
 */
public class GameState {
    private UUID gameId;
    private int turnsUsed;
    private int maxTurns;
    private boolean hasWon;
    private boolean isOver;

    public GameState() {}

    public GameState(UUID gameId, int turnsUsed, int maxTurns, boolean hasWon, boolean isOver) {
        this.gameId = gameId;
        this.turnsUsed = turnsUsed;
        this.maxTurns = maxTurns;
        this.hasWon = hasWon;
        this.isOver = isOver;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public int getTurnsUsed() {
        return turnsUsed;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public void setTurnsUsed(int turnsUsed) {
        this.turnsUsed = turnsUsed;
    }

    public boolean isHasWon() {
        return hasWon;
    }

    public void setHasWon(boolean hasWon) {
        this.hasWon = hasWon;
    }

    public boolean isIsOver() {
        return isOver;
    }

    public void setIsOver(boolean isOver) {
        this.isOver = isOver;
    }
}
