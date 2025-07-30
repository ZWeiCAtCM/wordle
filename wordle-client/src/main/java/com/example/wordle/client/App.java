package com.example.wordle.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

import com.example.wordle.WordleScorer.Mark;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App {

    private static final String SERVER_BASE = "http://localhost:8080";
    private static final ObjectMapper JSON = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (Scanner scanner = new Scanner(System.in)) {
            // 1. create a new game session
            HttpRequest createReq = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_BASE + "/games"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> createRes
                    = client.send(createReq, HttpResponse.BodyHandlers.ofString());
            JsonNode createJson = JSON.readTree(createRes.body());
            String gameId = createJson.get("gameId").asText();

            System.out.println("Welcome to Command-Line Wordle!");
            System.out.println("Feedback legend: [X] = correct letter & position, (X) = correct letter wrong position,  X  = letter not in word");
            System.out.println("New game started. Game ID: " + gameId);
            System.out.println();

            boolean isOver = false;
            while (!isOver) {
                // 2. fetch current game state
                HttpRequest stateReq = HttpRequest.newBuilder()
                        .uri(URI.create(SERVER_BASE + "/games/" + gameId))
                        .GET()
                        .build();
                HttpResponse<String> stateRes
                        = client.send(stateReq, HttpResponse.BodyHandlers.ofString());
                JsonNode stateJson = JSON.readTree(stateRes.body());

                int turnsUsed = stateJson.get("turnsUsed").asInt();
                int maxTurns = stateJson.get("maxTurns").asInt();
                isOver = stateJson.get("isOver").asBoolean();

                if (isOver) {
                    boolean hasWon = stateJson.get("hasWon").asBoolean();
                    if (hasWon) {
                        System.out.println("Congratulations! You won in " + turnsUsed + " turns.");
                    } else {
                        String answer = stateJson.has("answer")
                                ? stateJson.get("answer").asText()
                                : "<unknown>";
                        System.out.println("Game over. The correct word was: " + answer);
                    }
                    break;
                }

                // 3. prompt user with turn info
                System.out.printf("Enter your 5-letter guess (%d/%d): ", turnsUsed + 1, maxTurns);
                String guess = scanner.nextLine().trim().toLowerCase();
                if (guess.length() != 5) {
                    System.out.println("Please enter exactly 5 letters.");
                    continue;
                }

                // 4. submit guess
                String guessJson = "{\"guess\":\"" + guess + "\"}";
                HttpRequest guessReq = HttpRequest.newBuilder()
                        .uri(URI.create(SERVER_BASE + "/games/" + gameId + "/guesses"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(guessJson))
                        .build();
                HttpResponse<String> guessRes
                        = client.send(guessReq, HttpResponse.BodyHandlers.ofString());

                if (guessRes.statusCode() != 200) {
                    JsonNode errorJson = JSON.readTree(guessRes.body());
                    System.out.println(errorJson.get("message").asText());
                    // invalid guess, continue to next iteration
                    continue;
                }

                // 5. parse and display feedback
                JsonNode resJson = JSON.readTree(guessRes.body());
                JsonNode marksNode = resJson.get("marks");
                StringBuilder feedback = new StringBuilder();
                for (int i = 0; i < marksNode.size(); i++) {
                    Mark m = Mark.valueOf(marksNode.get(i).asText());
                    char c = guess.charAt(i);
                    switch (m) {
                        case HIT ->
                            feedback.append("[").append(c).append("]");
                        case PRESENT ->
                            feedback.append("(").append(c).append(")");
                        case MISS ->
                            feedback.append(" ").append(c).append(" ");
                    }
                }
                System.out.println(feedback);
            }
        }
    }
}
