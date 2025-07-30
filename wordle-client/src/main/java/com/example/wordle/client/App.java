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

        // Use try-with-resources for scanner
        try (Scanner scanner = new Scanner(System.in)) {
            // 1. Create a new game session
            HttpRequest createReq = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_BASE + "/games"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> createRes = client.send(createReq, HttpResponse.BodyHandlers.ofString());
            JsonNode createJson = JSON.readTree(createRes.body());
            String gameId = createJson.get("gameId").asText();
            System.out.println("New gameId: " + gameId);

            boolean isOver = false;
            while (!isOver) {
                System.out.print("Enter your 5-letter guess: ");
                String guess = scanner.nextLine().trim().toLowerCase();
                if (guess.length() != 5) {
                    System.out.println("Please enter exactly 5 letters.");
                    continue;
                }

                // 2. Submit guess to server
                String guessJson = "{\"guess\":\"" + guess + "\"}";
                HttpRequest guessReq = HttpRequest.newBuilder()
                        .uri(URI.create(SERVER_BASE + "/games/" + gameId + "/guesses"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(guessJson))
                        .build();
                HttpResponse<String> guessRes = client.send(guessReq, HttpResponse.BodyHandlers.ofString());

                if (guessRes.statusCode() != 200) {
                    System.out.println("Error: " + guessRes.body());
                    break;
                }

                JsonNode resJson = JSON.readTree(guessRes.body());
                JsonNode marksNode = resJson.get("marks");
                StringBuilder feedback = new StringBuilder();
                for (int i = 0; i < marksNode.size(); i++) {
                    Mark m = Mark.valueOf(marksNode.get(i).asText());
                    char c = guess.charAt(i);
                    switch (m) {
                        case HIT     -> feedback.append("[").append(c).append("]");
                        case PRESENT -> feedback.append("(").append(c).append(")");
                        case MISS    -> feedback.append(" ").append(c).append(" ");
                    }
                }
                System.out.println(feedback);

                isOver = resJson.get("isOver").asBoolean();
                boolean hasWon = resJson.get("hasWon").asBoolean();
                if (hasWon) {
                    System.out.println("Congratulations! You won in " + resJson.get("turnsUsed").asInt() + " turns.");
                } else if (isOver) {
                    System.out.println("Game over. Better luck next time. The correct word was: " + getAnswer(client, gameId));
                }
            }
        }
    }

    private static String getAnswer(HttpClient client, String gameId) throws Exception {
        HttpRequest stateReq = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_BASE + "/games/" + gameId))
                .GET()
                .build();
        HttpResponse<String> stateRes = client.send(stateReq, HttpResponse.BodyHandlers.ofString());
        JsonNode stateJson = JSON.readTree(stateRes.body());
        return stateJson.has("answer") ? stateJson.get("answer").asText() : "<unknown>";
    }
}
