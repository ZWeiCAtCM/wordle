package com.example.wordle.server.dto;

import java.util.UUID;

/**
 * snapshot of a single player's game state
 */
public record GameState(
    UUID playerId,   // use playerId for progress, gameId for legacy
    int turnsUsed,
    int maxTurns,
    boolean hasWon,
    boolean isOver,
    String answer    // revealed only when isOver is true
) {}
