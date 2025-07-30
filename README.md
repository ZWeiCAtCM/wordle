# Wordle Multi‑Module Project

This repository contains a multi‑module Maven project implementing a Wordle clone. It is structured into:

- **wordle-core**: Core game logic and data model.
- **wordle-cli**: A simple command‑line interface for playing Wordle (Task 1).
- **wordle-server**: REST API server for Task 2, handling game sessions and input validation.

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Modules](#modules)
- [Task 1: Command‑Line Interface](#task-1-command-line-interface)
- [Task 2: Server](#task-2-server)
- [Next Steps](#next-steps)

---

## Prerequisites

- Java 17 SDK (or later)
- Maven 3.6+
- Git

---

## Modules

### wordle-core

- Implements `WordleGame` and `WordleScorer`.
- Loads a 5‑letter word list from classpath (`src/main/resources/words.txt`).
- Supports configurable **maxTurns** and **wordFile** via constructors.

### wordle-cli

- Depends on **wordle-core**.
- Provides `App.java` for local CLI play (Task 1).
- Shows per-guess feedback:
  - **[X]** = correct letter & position
  - **(X)** = correct letter wrong position
  - \*\*  X  \*\* = letter not in word


### wordle-server

- Depends on **wordle-core** and Spring Boot.
- Exposes REST endpoints:
  - `POST /games` → create a new game, returns `{ "gameId": "..." }`
  - `POST /games/{id}/guesses` → submit a guess, returns marks & status
  - `GET  /games/{id}` → fetch current game state
- All answer logic and validation are performed server-side; clients never see the answer (Task 2).


### wordle-client

- Depends on **wordle-core** and uses Java HttpClient + Jackson.
- CLI client for Task 2:
  - Automatically fetches current `turnsUsed` and `maxTurns` before each guess
  - Submits guesses via `POST /games/{id}/guesses`
  - Handles invalid guesses by showing server's `message` and allowing retry
  - Displays final answer when game is over

---

## Task 1: Normal wordle

### Build & Test

From the project root:

```bash
mvn clean install
mvn -pl wordle-core test
```

### Running the CLI

```bash
mvn -pl wordle-cli exec:java
# or
java -jar wordle-cli/target/wordle-cli-1.0.0-SNAPSHOT.jar
```

Follow the prompt, enter a 5‑letter guess, and view feedback. A legend is shown on startup.

![CLI Screenshot](docs/wordle-cli.jpg)

---

## Task 2: Server/client wordle

### Build & Run Server

```bash
mvn clean install
mvn -pl wordle-server spring-boot:run
# or package and run:
# mvn -pl wordle-server clean package
# java -jar wordle-server/target/wordle-server-1.0.0-SNAPSHOT.jar
```

The server listens on [**http://localhost:8080**](http://localhost:8080).

### API Endpoints

| Method | Path                  | Description                          |
| ------ | --------------------- | ------------------------------------ |
| POST   | `/games`              | Create new game, returns `gameId`    |
| POST   | `/games/{id}/guesses` | Submit guess, returns marks & status |
| GET    | `/games/{id}`         | Retrieve current game state          |

### Examples

**Create game**

```bash
curl.exe -X POST http://localhost:8080/games
# {"gameId":"<uuid>"}
```

**Submit guess**

```bash
curl.exe -X POST http://localhost:8080/games/<uuid>/guesses \
  -H "Content-Type: application/json" \
  -d '{"guess":"apple"}'
# {"marks":["HIT","MISS",…],"hasWon":false,"isOver":false,"turnsUsed":1}
```

**Query state**

```bash
curl.exe http://localhost:8080/games/<uuid>
# {"gameId":"<uuid>","turnsUsed":1,"hasWon":false,"isOver":false}
```
### Run Client

```bash
mvn -pl wordle-client exec:java
```

Client interacts entirely via HTTP to the server.
A game success looks like:
![CLI Screenshot](docs/wordle-client1.jpg)

A game failure looks like:
![CLI Screenshot](docs/wordle-client2.jpg)

---

## Next Steps

- **Task 3**: Add a “cheater” service to suggest optimal next guesses.
- **Task 4**: Implement multiplayer mode (e.g., via WebSocket).
- **wordle-frontend**: Build a web UI (React, Angular, etc.).

Contributions and feedback are welcome!

