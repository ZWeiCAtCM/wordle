/*
 * src/test/java/com/example/wordle/WordleScorerTest.java
 */
package com.example.wordle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link WordleScorer} validating scoring logic
 * for exact matches, complete misses, all-present cases,
 * and mixed scenarios.
 */
class WordleScorerTest {

    /**
     * Test that scoring a guess identical to the answer
     * yields all HIT marks.
     */
    @Test
    void testExactMatch() {
        WordleScorer scorer = new WordleScorer();
        String guess = "apple";
        String answer = "apple";
        WordleScorer.Mark[] marks = scorer.score(guess, answer);
        assertEquals(5, marks.length, "Result array must have length 5");
        for (WordleScorer.Mark mark : marks) {
            assertEquals(WordleScorer.Mark.HIT, mark,
                    "Each mark should be HIT for exact match");
        }
    }

    /**
     * Test that scoring a guess with no letters in common
     * with the answer yields all MISS marks.
     */
    @Test
    void testAllMiss() {
        WordleScorer scorer = new WordleScorer();
        String guess = "abcde";
        String answer = "fghij";
        WordleScorer.Mark[] marks = scorer.score(guess, answer);
        for (WordleScorer.Mark mark : marks) {
            assertEquals(WordleScorer.Mark.MISS, mark,
                    "Each mark should be MISS when no letters match");
        }
    }

    /**
     * Test that scoring a guess with correct letters but wrong positions
     * yields all PRESENT marks.
     */
    @Test
    void testAllPresent() {
        WordleScorer scorer = new WordleScorer();
        String guess = "alert";
        String answer = "later";
        WordleScorer.Mark[] marks = scorer.score(guess, answer);
        for (WordleScorer.Mark mark : marks) {
            assertEquals(WordleScorer.Mark.PRESENT, mark,
                    "Each mark should be PRESENT when letters exist but are misplaced");
        }
    }

    /**
     * Test a mixed scenario where some letters are correct,
     * some are present but misplaced, and some are misses.
     */
    @Test
    void testMixedCase() {
        WordleScorer scorer = new WordleScorer();
        String guess = "apple";
        String answer = "angle";
        WordleScorer.Mark[] marks = scorer.score(guess, answer);
        assertEquals(WordleScorer.Mark.HIT,    marks[0], "Letter 'a' should be HIT");
        assertEquals(WordleScorer.Mark.MISS,   marks[1], "First 'p' should be MISS");
        assertEquals(WordleScorer.Mark.MISS,   marks[2], "Second 'p' should be MISS");
        assertEquals(WordleScorer.Mark.HIT,    marks[3], "Letter 'l' should be HIT");
        assertEquals(WordleScorer.Mark.HIT,    marks[4], "Letter 'e' should be HIT");
    }
}
