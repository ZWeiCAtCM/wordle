package com.example.wordle;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CheatingWordleGameTest {

    private static final String SIMPLE_WORD_FILE = "test-words-simple.txt";
    private CheatingWordleGame game;

    @BeforeEach
    public void setUp() throws IOException {
        game = new CheatingWordleGame(6, SIMPLE_WORD_FILE);
    }

    @Test
    void initialCandidatesShouldMatchWordListSize() throws Exception {
        List<String> all = WordleGame.loadWordList(SIMPLE_WORD_FILE);
        List<String> candidates = getCandidates(game);
        assertEquals(all.size(), candidates.size());
        assertEquals(all.size(), 3);
    }

    @Test
    void singleGuessReducesToMissBucket() throws Exception {
        // first guess "panic" should yield all MISS and leave only "buggy"
        assertFalse(game.guess("panic"));
        WordleScorer.Mark[] marks = game.getLastMarks();
        for (WordleScorer.Mark m : marks) {
            assertEquals(WordleScorer.Mark.MISS, m);
        }
        List<String> cands = getCandidates(game);
        assertEquals(1, cands.size());
        assertEquals("buggy", cands.get(0));
    }

    @Test
    void winConditionWhenOneCandidateLeft() throws Exception {
        // after filtering, only "buggy" remains, so guessing "buggy" wins
        assertFalse(game.guess("panic"));
        assertTrue(game.guess("buggy"));
        assertTrue(game.hasWon());
        assertTrue(game.isOver());
    }

    @Test
    void lossWhenMaxTurnsExceeded() throws Exception {
        CheatingWordleGame oneTurn = new CheatingWordleGame(1, SIMPLE_WORD_FILE);
        assertFalse(oneTurn.guess("panic"));
        assertTrue(oneTurn.isOver());
        assertEquals("buggy", oneTurn.getAnswer());
    }

    @Test
    void invalidGuessThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> game.guess("xxxxx"));
        assertThrows(IllegalArgumentException.class,
            () -> game.guess("toolong"));
    }

    @SuppressWarnings("unchecked")
    private List<String> getCandidates(CheatingWordleGame g) throws Exception {
        Field f = CheatingWordleGame.class.getDeclaredField("candidates");
        f.setAccessible(true);
        return (List<String>) f.get(g);
    }
}
