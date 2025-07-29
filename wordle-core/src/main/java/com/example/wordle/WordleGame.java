package com.example.wordle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Manages a single-player Wordle game session with configurable options.
 */
public class WordleGame {
    private final int maxTurns;
    private final List<String> wordList;
    private String answer;
    private int turnsUsed = 0;
    private boolean won = false;
    private WordleScorer.Mark[] lastMarks;

    /**
     * Constructor: load wordList from classpath and pick a random answer.
     * @param maxTurns maximum number of guesses allowed before game over
     * @param wordFile name of the word list resource (e.g. "words.txt")
     * @throws IOException if the word list cannot be loaded
     */
    public WordleGame(int maxTurns, String wordFile) throws IOException {
        this.maxTurns = maxTurns;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(wordFile);
             BufferedReader reader = new BufferedReader(
                 new InputStreamReader(is, StandardCharsets.UTF_8))) {
            if (is == null) {
                throw new IllegalStateException(wordFile + " not found on classpath");
            }
            this.wordList = reader.lines().collect(Collectors.toList());
        }
        this.answer = wordList.get(new Random().nextInt(wordList.size()));
    }

    /**
     * Test constructor: load wordList and use the provided answer.
     * @param maxTurns maximum number of guesses allowed
     * @param wordFile resource name for word list
     * @param answer the word to be guessed (must exist in the list)
     * @throws IOException if the word list cannot be loaded
     */
    public WordleGame(int maxTurns, String wordFile, String answer) throws IOException {
        this(maxTurns, wordFile);
        if (!wordList.contains(answer)) {
            throw new IllegalArgumentException("Answer must be in word list");
        }
        this.answer = answer;
    }

    /**
     * Make a guess. Records marks and returns true if correct.
     * @param word a valid 5-letter word
     * @return true if guess equals the answer
     */
    public boolean guess(String word) {
        if (turnsUsed >= maxTurns || won) {
            throw new IllegalStateException("Game over");
        }
        if (word == null || word.length() != 5 || !wordList.contains(word)) {
            throw new IllegalArgumentException("Invalid guess");
        }
        turnsUsed++;
        lastMarks = new WordleScorer().score(word, answer);
        if (word.equals(answer)) {
            won = true;
        }
        return won;
    }

    /**
     * @return true if the game is finished (win or max turns reached)
     */
    public boolean isOver() {
        return won || turnsUsed >= maxTurns;
    }

    /**
     * @return true if the player has guessed correctly
     */
    public boolean hasWon() {
        return won;
    }

    /**
     * @return the number of turns used so far
     */
    public int getTurnsUsed() {
        return turnsUsed;
    }

    /**
     * @return the marks from the most recent guess
     */
    public WordleScorer.Mark[] getLastMarks() {
        return lastMarks;
    }

    /**
     * @return the secret answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * @return maximum number of turns allowed
     */
    public int getMaxTurns() {
        return maxTurns;
    }

    /**
     * @return the loaded word list
     */
    public List<String> getWordList() {
        return wordList;
    }
}
