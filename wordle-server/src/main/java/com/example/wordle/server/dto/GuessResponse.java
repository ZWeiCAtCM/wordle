package com.example.wordle.server.dto;

import com.example.wordle.WordleScorer.Mark;

/**
 * Response after a guess.
 */
public class GuessResponse {
    private Mark[] marks;
    private boolean hasWon;
    private boolean isOver;
    private int turnsUsed;
    private int maxTurns;

    // Jackson needs a no-arg constructor
    public GuessResponse() {}

    public GuessResponse(Mark[] marks, boolean hasWon, boolean isOver, int turnsUsed, int maxTurns) {
        this.marks = marks;
        this.hasWon = hasWon;
        this.isOver = isOver;
        this.turnsUsed = turnsUsed;
        this.maxTurns = maxTurns;
    }

    public Mark[] getMarks() {
        return marks;
    }

    public void setMarks(Mark[] marks) {
        this.marks = marks;
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

    public int getTurnsUsed() {
        return turnsUsed;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public void setTurnsUsed(int turnsUsed) {
        this.turnsUsed = turnsUsed;
    }
}
