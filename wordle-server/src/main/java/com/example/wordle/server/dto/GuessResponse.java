package com.example.wordle.server.dto;

import com.example.wordle.WordleScorer.Mark;

/**
 * response returned after each guess
 */
public record GuessResponse(
    Mark[] marks,
    boolean hasWon,
    boolean isOver,
    int turnsUsed,
    int maxTurns,
    String answer  // null until game is over
) {}
