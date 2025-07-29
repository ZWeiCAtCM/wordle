package com.example.wordle;

/**
 * Scores a Wordle guess against the answer.
 */
public class WordleScorer {
    public enum Mark { HIT, PRESENT, MISS }

    /**
     * Score a guess against the answer.
     * @param guess  the guessed word
     * @param answer the target word
     * @return array of Marks indicating letter status
     */
    public Mark[] score(String guess, String answer) {
        Mark[] result = new Mark[5];
        boolean[] used = new boolean[5];

        // First pass: correct position
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                result[i] = Mark.HIT;
                used[i] = true;
            }
        }
        // Second pass: present but wrong position
        for (int i = 0; i < 5; i++) {
            if (result[i] == null) {
                char c = guess.charAt(i);
                boolean found = false;
                for (int j = 0; j < 5; j++) {
                    if (!used[j] && answer.charAt(j) == c) {
                        found = true;
                        used[j] = true;
                        break;
                    }
                }
                result[i] = found ? Mark.PRESENT : Mark.MISS;
            }
        }
        return result;
    }
}
