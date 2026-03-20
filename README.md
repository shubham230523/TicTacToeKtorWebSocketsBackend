# TicTacToeKtorWebSocketsBackend 🕹️

A robust, real-time multiplayer backend for Tic-Tac-Toe, built with the **Ktor** framework and **WebSockets**. This server manages game states, player sessions, and move validation to ensure a seamless, synchronized gaming experience across multiple clients.

---

## 🌟 Features

* **Real-time Multiplayer:** Leverages WebSockets for low-latency, bidirectional communication between the server and players.
* **State Management:** Server-side authoritative logic to manage the 3x3 grid, preventing illegal moves or out-of-sync states.
* **Player Sessions:** Automatic session management to identify players and track active game instances.
* **Game Logic Engine:** * Validates player turns.
    * Detects win conditions (rows, columns, diagonals).
    * Handles draw scenarios.
* **JSON Protocol:** Clean data exchange using **Kotlin Serialization** for game actions and state updates.

---

## 🛠 Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Kotlin |
| **Framework** | Ktor |
| **Communication** | WebSockets |
| **Serialization** | Kotlinx Serialization |
| **Concurrency** | Kotlin Coroutines |
| **Logging** | Logback |

---

## 🏗 How It Works

1.  **Connection:** When a player connects, the server assigns a unique session and waits for a second player to join.
2.  **Turn Logic:** The server maintains an internal `TicTacToeGame` object. It only accepts moves from the player whose turn it is.
3.  **Broadcast:** Once a valid move is made, the server updates the game state and broadcasts the updated board to both connected clients via the WebSocket session.
4.  **Completion:** The server identifies if a move results in a win or draw and sends a final game-over state before closing or resetting the session.

---

## 🚀 Getting Started

### Prerequisites
* **IntelliJ IDEA** (Recommended) or Android Studio
* **JDK 17+**

### Installation & Run
1. **Clone the repository:**
   ```bash
   git clone https://github.com/shubham230523/TicTacToeKtorWebSocketsBackend.git
