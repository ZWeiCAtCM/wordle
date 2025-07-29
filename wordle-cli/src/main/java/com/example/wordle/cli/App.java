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
        WordleGame game = new WordleGame();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Command-Line Wordle!");
        // Print feedback legend
        System.out.println("Feedback legend: [X] = correct letter & position, (X) = correct letter wrong position,  X  = letter not in word");
        System.out.println();

        while (!game.isOver()) {
            System.out.print("Enter your 5-letter guess: ");
            String guess = scanner.nextLine().trim().toLowerCase();

            if (guess.length() != 5) {
                System.out.println("Please enter exactly 5 letters.");
                continue;
            }

            try {
                boolean win = game.guess(guess);
                WordleScorer.Mark[] marks = game.getLastMarks();
                // Display feedback: [X] for hit, (X) for present,  X  for miss
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
                    System.out.println("Congratulations! You guessed the word in "
                            + game.getTurnsUsed() + " turns.");
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
