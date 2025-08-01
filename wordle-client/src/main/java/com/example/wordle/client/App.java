package com.example.wordle.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Scanner;

import com.example.wordle.WordleScorer.Mark;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * command-line client that runs two players (A and B) in one terminal,
 * alternating turns, showing feedback per guess, and declaring a winner
 */
public class App {

    private static final String SERVER_BASE = "http://localhost:8080";
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            // 1. create a new game session
            JsonNode createJson = post("/games", "");
            String gameId = createJson.get("gameId").asText();
            System.out.println("New game started. Game ID: " + gameId);
            System.out.println("Feedback legend: [X] = correct & correct pos, (X) = correct wrong pos,  X  = not in word");
            System.out.println();

            // 2. both players join and get their IDs
            String playerAId = post("/games/" + gameId + "/join", "").get("playerId").asText();
            System.out.println("Player A joined with ID: " + playerAId);
            String playerBId = post("/games/" + gameId + "/join", "").get("playerId").asText();
            System.out.println("Player B joined with ID: " + playerBId);
            System.out.println();

            boolean gameOver = false;
            String current = "A";

            // 3. alternate turns until someone wins or turns run out
            while (!gameOver) {
                String pid = current.equals("A") ? playerAId : playerBId;

                // 3.1 check this player's state before guessing
                HttpRequest stateReq = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_BASE + "/games/" + gameId))
                    .timeout(Duration.ofSeconds(5))
                    .header("X-Player-Id", pid)
                    .GET()
                    .build();
                HttpResponse<String> stateRes = CLIENT.send(stateReq, HttpResponse.BodyHandlers.ofString());
                JsonNode stateJson = JSON.readTree(stateRes.body());
                boolean isOver = stateJson.get("isOver").asBoolean();
                boolean hasWon = stateJson.get("hasWon").asBoolean();
                if (isOver) {
                    if (hasWon) {
                        System.out.printf("Player %s already won.%n", current);
                    } else {
                        System.out.printf("Player %s has no turns left.%n", current);
                    }
                    // if both players have finished, end game loop
                    if (current.equals("B")) {
                        System.out.println("Both players have finished. Game over.");
                        gameOver = true;
                    } else {
                        // switch to next player
                        current = "B";
                    }
                    continue;
                }

                // 3.2 prompt current player
                System.out.printf("Player %s, enter your 5-letter guess: ", current);
                String guess = scanner.nextLine().trim().toLowerCase();
                if (guess.length() != 5) {
                    System.out.println("Please enter exactly 5 letters.");
                    continue;
                }

                // 4. submit guess with player header
                HttpRequest guessReq = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_BASE + "/games/" + gameId + "/guesses"))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .header("X-Player-Id", pid)
                    .POST(HttpRequest.BodyPublishers.ofString("{\"guess\":\"" + guess + "\"}"))
                    .build();

                HttpResponse<String> guessRes = CLIENT.send(guessReq, HttpResponse.BodyHandlers.ofString());
                int status = guessRes.statusCode();
                if (status == 400) {
                    // invalid guess
                    String msg = JSON.readTree(guessRes.body()).get("message").asText();
                    System.out.println("Invalid guess: " + msg);
                    continue;
                }
                if (status != 200) {
                    // other error (e.g. game over)
                    System.out.println("Error: HTTP " + status);
                    break;
                }

                // 5. parse result
                JsonNode res = JSON.readTree(guessRes.body());
                JsonNode marks = res.get("marks");
                boolean won = res.get("hasWon").asBoolean();
                isOver = res.get("isOver").asBoolean();

                // 6. display feedback
                StringBuilder feedback = new StringBuilder();
                for (int i = 0; i < marks.size(); i++) {
                    Mark m = Mark.valueOf(marks.get(i).asText());
                    char c = guess.charAt(i);
                    switch (m) {
                        case HIT    -> feedback.append("[").append(c).append("]");
                        case PRESENT-> feedback.append("(").append(c).append(")");
                        case MISS   -> feedback.append(" ").append(c).append(" ");
                    }
                }
                System.out.println(feedback);

                // 7. handle win/turns-exhausted
                if (won) {
                    System.out.printf("Player %s WINS! The word was: %s%n", current,
                        res.has("answer") ? res.get("answer").asText() : "<unknown>");
                    gameOver = true;
                } else if (isOver) {
                    System.out.printf("Player %s has used all turns.%n", current);
                    if (current.equals("B")) {
                        System.out.println("Both players have no turns left. Game over, it's a tie.");
                        gameOver = true;
                    }
                }
                // switch to next player
                current = current.equals("A") ? "B" : "A";
            }
        }
    }

    /**
     * helper: send POST and return parsed JSON
     */
    private static JsonNode post(String path, String body) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(SERVER_BASE + path))
            .timeout(Duration.ofSeconds(5))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        HttpResponse<String> r = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        return JSON.readTree(r.body());
    }
}
