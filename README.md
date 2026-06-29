# Chess Master

A complete, desktop Chess game built in Java 1.7 using **Java Swing** for the GUI. It features standard chess gameplay and a multi-account management system.

##  Features & Architecture

The source code (`src`) is cleanly organized into 4 main packages:

- **`pieces`:** Implements chess pieces using OOP principles (Inheritance and Polymorphism). Each piece overrides its own movement logic.
- **`gui`:** Handles the visual layer using Java Swing, rendering the chess board and account screens.
- **`logic`:** Coordinated the core game rules, turn management, and user account validation.
- **`exception`:** Manages custom exceptions for safe error handling (e.g., illegal moves).

##  How to Run

1. Open this project in your preferred IDE (like IntelliJ IDEA).
2. Ensure your project structure points to Java 1.7 (or a compatible SDK).
3. Navigate to the **`logic`** package and run **`Main.java`** to launch the game.
