/*
 * src/test/java/com/example/wordle/WordleGameTest.java
 */
package com.example.wordle;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {

    @Test
    void testWinningScenario() throws IOException {
        WordleGame game = new WordleGame("apple");
        assertFalse(game.isOver());
        assertFalse(game.hasWon());

        boolean result = game.guess("apple");
        assertTrue(result);
        assertTrue(game.hasWon());
        assertTrue(game.isOver());

        WordleScorer.Mark[] marks = game.getLastMarks();
        for (WordleScorer.Mark mark : marks) {
            assertEquals(WordleScorer.Mark.HIT, mark);
        }
    }

    @Test
    void testInvalidGuess() throws IOException {
        WordleGame game = new WordleGame("apple");
        assertThrows(IllegalArgumentException.class, () -> game.guess("app"));
        assertThrows(IllegalArgumentException.class, () -> game.guess("zzzzz"));
    }

    @Test
    void testLosingAfterMaxTurns() throws IOException {
        WordleGame game = new WordleGame("apple");
        for (int i = 0; i < 6; i++) {
            assertFalse(game.hasWon());
            game.guess("bench"); // 'bench' must be in words.txt
        }
        assertTrue(game.isOver());
        assertFalse(game.hasWon());
        assertThrows(IllegalStateException.class, () -> game.guess("bench"));
    }
}
