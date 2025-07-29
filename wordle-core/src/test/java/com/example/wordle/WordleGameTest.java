/*
 * src/test/java/com/example/wordle/WordleGameTest.java
 */
package com.example.wordle;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link WordleGame} covering winning, invalid input,
 * and losing scenarios.
 */
class WordleGameTest {

    /**
     * Test that a correct guess immediately wins the game,
     * marks all letters as HIT, and flags the game as over.
     */
    @Test
    void testWinningScenario() throws IOException {
        WordleGame game = new WordleGame("apple");
        assertFalse(game.isOver(), "Game should not be over before any guess");
        assertFalse(game.hasWon(), "Game should not be won before any guess");

        boolean result = game.guess("apple");
        assertTrue(result, "guess() should return true for correct answer");
        assertTrue(game.hasWon(), "hasWon() should return true after correct guess");
        assertTrue(game.isOver(), "isOver() should return true after winning");

        WordleScorer.Mark[] marks = game.getLastMarks();
        for (WordleScorer.Mark mark : marks) {
            assertEquals(WordleScorer.Mark.HIT, mark,
                    "All marks should be HIT for a correct guess");
        }
    }

    /**
     * Test that guesses of incorrect length or words not in the list
     * throw an IllegalArgumentException.
     */
    @Test
    void testInvalidGuess() throws IOException {
        WordleGame game = new WordleGame("apple");
        assertThrows(IllegalArgumentException.class,
                () -> game.guess("app"),
                "Guess shorter than 5 letters should throw IllegalArgumentException");
        assertThrows(IllegalArgumentException.class,
                () -> game.guess("zzzzz"),
                "Guess not present in word list should throw IllegalArgumentException");
    }

    /**
     * Test that using the maximum number of turns without guessing correctly
     * ends the game, and further guesses throw an IllegalStateException.
     */
    @Test
    void testLosingAfterMaxTurns() throws IOException {
        WordleGame game = new WordleGame("apple");
        for (int i = 0; i < 6; i++) {
            assertFalse(game.hasWon(), "Game should not be won before max turns");
            game.guess("bench"); // 'bench' must be present in words.txt
        }
        assertTrue(game.isOver(), "isOver() should return true after max turns");
        assertFalse(game.hasWon(), "hasWon() should return false if answer not guessed");
        assertThrows(IllegalStateException.class,
                () -> game.guess("bench"),
                "Further guesses after game over should throw IllegalStateException");
    }
}
