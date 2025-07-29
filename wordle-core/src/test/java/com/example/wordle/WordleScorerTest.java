/*
 * src/test/java/com/example/wordle/WordleScorerTest.java
 */
package com.example.wordle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WordleScorerTest {

    @Test
    void testExactMatch() {
        WordleScorer scorer = new WordleScorer();
        String guess = "apple";
        String answer = "apple";
        WordleScorer.Mark[] marks = scorer.score(guess, answer);
        assertEquals(5, marks.length);
        for (WordleScorer.Mark mark : marks) {
            assertEquals(WordleScorer.Mark.HIT, mark);
        }
    }

    @Test
    void testAllMiss() {
        WordleScorer scorer = new WordleScorer();
        String guess = "abcde";
        String answer = "fghij";
        WordleScorer.Mark[] marks = scorer.score(guess, answer);
        for (WordleScorer.Mark mark : marks) {
            assertEquals(WordleScorer.Mark.MISS, mark);
        }
    }

    @Test
    void testAllPresent() {
        WordleScorer scorer = new WordleScorer();
        String guess = "alert";
        String answer = "later";
        WordleScorer.Mark[] marks = scorer.score(guess, answer);
        for (WordleScorer.Mark mark : marks) {
            assertEquals(WordleScorer.Mark.PRESENT, mark);
        }
    }

    @Test
    void testMixedCase() {
        WordleScorer scorer = new WordleScorer();
        String guess = "apple";
        String answer = "angle";
        WordleScorer.Mark[] marks = scorer.score(guess, answer);
        assertEquals(WordleScorer.Mark.HIT, marks[0]);  // a
        assertEquals(WordleScorer.Mark.MISS, marks[1]); // p
        assertEquals(WordleScorer.Mark.MISS, marks[2]); // p
        assertEquals(WordleScorer.Mark.HIT, marks[3]);  // l
        assertEquals(WordleScorer.Mark.HIT, marks[4]);  // e
    }
}