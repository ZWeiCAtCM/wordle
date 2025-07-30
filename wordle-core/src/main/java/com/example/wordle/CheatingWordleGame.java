package com.example.wordle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A WordleGame variant that “cheats” by dynamically
 * keeping the answer as any word that yields the least
 * helpful feedback for the player.
 */
public class CheatingWordleGame extends WordleGame {
    private List<String> candidates;

    public CheatingWordleGame(int maxTurns, String wordFile) throws IOException {
        super(maxTurns, loadWordList(wordFile));
        this.candidates = new ArrayList<>(getWordList());
    }

    @Override
    public boolean guess(String word) {
        if (isOver()) {
            throw new IllegalStateException("Game over");
        }
        if (word == null || word.length() != 5 || !getWordList().contains(word)) {
            throw new IllegalArgumentException(
                "Invalid guess: must be 5 letters and in the word list");
        }

        // 1. Increment turn
        turnsUsed++;

        // 2. Bucket candidates by the feedback pattern
        Map<String, List<String>> buckets = new HashMap<>();
        WordleScorer scorer = new WordleScorer();
        for (String cand : candidates) {
            WordleScorer.Mark[] marks = scorer.score(word, cand);
            String key = patternKey(marks);
            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(cand);
        }

        // 3. Pick the “least helpful” pattern: fewest hits, then fewest presents
        String bestKey = buckets.keySet().stream()
            .min(Comparator
                .comparingInt(this::countHits)
                .thenComparingInt(this::countPresents))
            .orElseThrow();

        // 4. Update candidates to that bucket
        candidates = buckets.get(bestKey);

        // 5. Update lastMarks for external inspection
        this.lastMarks = parsePatternKey(bestKey);

        // 6. Win condition: all hits
        if (isAllHit(this.lastMarks)) {
            this.won = true;
            return true;
        }

        // 7. Loss condition: out of turns
        if (turnsUsed >= maxTurns) {
            // expose a final answer from the remaining candidates
            this.answer = candidates.get(0);
            return false;
        }

        return false;
    }

    private String patternKey(WordleScorer.Mark[] marks) {
        return Arrays.stream(marks)
                     .map(Enum::name)
                     .collect(Collectors.joining(","));
    }

    private WordleScorer.Mark[] parsePatternKey(String key) {
        String[] parts = key.split(",");
        WordleScorer.Mark[] marks = new WordleScorer.Mark[parts.length];
        for (int i = 0; i < parts.length; i++) {
            marks[i] = WordleScorer.Mark.valueOf(parts[i]);
        }
        return marks;
    }

    private int countHits(String key) {
        return (int) Arrays.stream(parsePatternKey(key))
                           .filter(m -> m == WordleScorer.Mark.HIT)
                           .count();
    }

    private int countPresents(String key) {
        return (int) Arrays.stream(parsePatternKey(key))
                           .filter(m -> m == WordleScorer.Mark.PRESENT)
                           .count();
    }

    private boolean isAllHit(WordleScorer.Mark[] marks) {
        for (WordleScorer.Mark m : marks) {
            if (m != WordleScorer.Mark.HIT) return false;
        }
        return true;
    }
}
