package com.example.wordle.cli;

import java.io.IOException;
import java.util.Scanner;

import com.example.wordle.WordleGame;
import com.example.wordle.WordleScorer;

/**
 * Command-line interface for playing Wordle.
 */
public class App {
    public static void main(String[] args) throws IOException {
        // Configuration: maxRounds and word list file can be customized via args or defaults
        int maxRounds = 6;
        String wordFile = "words.txt";
        WordleGame game = new WordleGame(maxRounds, wordFile);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Command-Line Wordle!");
        System.out.println("Feedback legend: [X] = correct letter & position, (X) = correct letter wrong position,  X  = letter not in word");
        System.out.println();

        while (!game.isOver()) {
            System.out.printf("Enter your 5-letter guess (%d/%d): ", game.getTurnsUsed() + 1, game.getMaxTurns());
            String guess = scanner.nextLine().trim().toLowerCase();

            if (guess.length() != 5) {
                System.out.println("Please enter exactly 5 letters.");
                continue;
            }

            try {
                boolean win = game.guess(guess);
                WordleScorer.Mark[] marks = game.getLastMarks();
                // Build feedback string
                StringBuilder feedback = new StringBuilder();
                for (int i = 0; i < marks.length; i++) {
                    char c = guess.charAt(i);
                    switch (marks[i]) {
                        case HIT:
                            feedback.append("[").append(c).append("]");
                            break;
                        case PRESENT:
                            feedback.append("(").append(c).append(")");
                            break;
                        case MISS:
                            feedback.append(" ").append(c).append(" ");
                            break;
                    }
                }
                System.out.println(feedback.toString());

                if (win) {
                    System.out.println("🎉 Congratulations! You guessed the word in " + game.getTurnsUsed() + " turns.");
                    break;
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }

        if (!game.hasWon()) {
            System.out.println("Game over! The correct word was: " + game.getAnswer());
        }
        scanner.close();
    }
}
