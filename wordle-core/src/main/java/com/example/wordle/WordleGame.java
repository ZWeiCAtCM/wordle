package com.example.wordle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * Manages a single-player Wordle game session.
 */
public class WordleGame {
    private static final int MAX_TURNS = 6;
    private final List<String> wordList;
    private String answer;
    private int turnsUsed = 0;
    private boolean won = false;
    private WordleScorer.Mark[] lastMarks;    // ← store last guess result

    /**
     * Constructor: load wordList and pick a random answer.
     */
    public WordleGame() throws IOException {
        wordList = Files.readAllLines(Paths.get("src/main/resources/words.txt"));
        answer = wordList.get(new Random().nextInt(wordList.size()));
    }

    /**
     * Constructor for test: load wordList and use specific answer.
     */
    public WordleGame(String answer) throws IOException {
        wordList = Files.readAllLines(Paths.get("src/main/resources/words.txt"));
        if (!wordList.contains(answer)) {
            throw new IllegalArgumentException("Answer must be in word list");
        }
        this.answer = answer;
    }

    /**
     * Make a guess. Records marks and returns true if correct.
     * @param word a valid 5‑letter word
     * @return true if guess equals the answer
     */
    public boolean guess(String word) {
        if (turnsUsed >= MAX_TURNS || won) {
            throw new IllegalStateException("Game over");
        }
        if (word == null || word.length() != 5 || !wordList.contains(word)) {
            throw new IllegalArgumentException("Invalid guess");
        }

        turnsUsed++;
        WordleScorer scorer = new WordleScorer();
        lastMarks = scorer.score(word, answer);  // ← store the marks
        if (word.equals(answer)) {
            won = true;
        }
        return won;
    }

    /** @return true if the game is finished (win or used all turns) */
    public boolean isOver() {
        return won || turnsUsed >= MAX_TURNS;
    }

    /** @return true if the player has guessed correctly */
    public boolean hasWon() {
        return won;
    }

    /** @return the number of turns used so far */
    public int getTurnsUsed() {
        return turnsUsed;
    }

    /** @return the marks from the most recent guess */
    public WordleScorer.Mark[] getLastMarks() {
        return lastMarks;
    }

    /** @return the secret answer */
    public String getAnswer() {
        return answer;
    }
}
