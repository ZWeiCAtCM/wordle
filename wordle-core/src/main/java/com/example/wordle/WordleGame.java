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
    protected final int maxTurns;
    protected final List<String> wordList;
    private String wordFile;
    protected String answer;
    protected int turnsUsed = 0;
    protected boolean won = false;
    protected WordleScorer.Mark[] lastMarks;

    /**
     * Load the word list from classpath.
     */
    public static List<String> loadWordList(String wordFile) throws IOException {
        try (InputStream is = WordleGame.class.getClassLoader().getResourceAsStream(wordFile);
             BufferedReader reader = new BufferedReader(
                 new InputStreamReader(is, StandardCharsets.UTF_8))) {
            if (is == null) {
                throw new IllegalStateException(wordFile + " not found on classpath");
            }
            return reader.lines().collect(Collectors.toList());
        }
    }

    /**
     * Constructor: load wordList from classpath and pick a random answer.
     */
    public WordleGame(int maxTurns, String wordFile) throws IOException {
        this(maxTurns, loadWordList(wordFile));
        this.wordFile = wordFile;
    }

    /**
     * Protected constructor: accept pre-loaded wordList and pick random answer.
     */
    protected WordleGame(int maxTurns, List<String> wordList) {
        this.maxTurns = maxTurns;
        this.wordList  = wordList;
        this.answer    = wordList.get(new Random().nextInt(wordList.size()));
    }

    /**
     * Test constructor: load wordList and use the provided answer.
     */
    public WordleGame(int maxTurns, String wordFile, String answer) throws IOException {
        this(maxTurns, loadWordList(wordFile));
        if (!wordList.contains(answer)) {
            throw new IllegalArgumentException("Answer must be in word list");
        }
        this.answer = answer;
    }

    /**
     * Make a guess. Records marks and returns true if correct.
     */
    public boolean guess(String word) {
        if (turnsUsed >= maxTurns || won) {
            throw new IllegalStateException("Game over");
        }
        if (word == null || word.length() != 5 || !wordList.contains(word)) {
            throw new IllegalArgumentException(
                "Invalid guess: must be 5 letters and in the word list");
        }
        turnsUsed++;
        lastMarks = new WordleScorer().score(word, answer);
        if (word.equals(answer)) {
            won = true;
        }
        return won;
    }

    public boolean isOver() {
        return won || turnsUsed >= maxTurns;
    }

    public boolean hasWon() {
        return won;
    }

    public int getTurnsUsed() {
        return turnsUsed;
    }

    public WordleScorer.Mark[] getLastMarks() {
        return lastMarks;
    }

    public String getAnswer() {
        return answer;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public List<String> getWordList() {
        return wordList;
    }
    
    public String getWordFile() {
        return wordFile;
    }
}
