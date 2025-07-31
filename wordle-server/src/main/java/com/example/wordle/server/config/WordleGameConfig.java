package com.example.wordle.server.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.wordle.CheatingWordleGame;
import com.example.wordle.WordleGame;

@Configuration
public class WordleGameConfig {

    @Value("${wordle.mode}")
    private String mode;

    @Value("${wordle.maxTurns}")
    private int maxTurns;

    @Value("${wordle.wordFile}")
    private String wordFile;

    /**
     * create either a normal or cheating WordleGame based on mode
     */
    @Bean
    public WordleGame gamePrototype() throws IOException {
        if ("cheat".equalsIgnoreCase(mode)) {
            return new CheatingWordleGame(maxTurns, wordFile);
        }
        return new WordleGame(maxTurns, wordFile);
    }
}
