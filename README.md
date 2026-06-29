# ChessMaster

A complete, interactive desktop Chess game built in Java 1.7. This project features a custom graphical user interface (GUI) and a multi-account management system, structured using clean Object-Oriented Programming (OOP) principles.

## Features

- **Complete Chess Game:** Implements all standard chess moves, rules, and win/draw conditions.
- **Graphical User Interface (GUI):** A clean visual board for smooth local gameplay and user interaction.
- **Multi-Account System:** Allows users to create and manage multiple personal profiles.
- **Custom Exception Handling:** Robust error management for invalid moves or account operations.
- **Java 1.7 Compatibility:** Developed using stable Java 1.7 features, focusing on classic software engineering practices.

## Project Architecture & Design Patterns

The source code (`src`) is modularly organized into 4 main packages, reflecting a **Model-View-Controller (MVC)** architectural style:

- **`pieces` (Model):** Implements game components using **Inheritance** and **Polymorphism**. Each piece inherits from a base class and overrides its specific movement logic.
- **`gui` (View):** Handles the visual presentation layer, rendering the chess board, pieces, and account management screens.
- **`logic` (Controller):** Contains the core business logic, coordinating the game flow, turn management, and validating user accounts.
- **`exception`:** Houses custom application exceptions, ensuring the system fails gracefully during illegal moves or registration conflicts.

## How to Run

1. Open this project in your preferred IDE (like IntelliJ IDEA).
2. Ensure your project structure points to Java 1.7 (or a compatible SDK).
3. Navigate to the **`logic`** package and run **`Main.java`** to launch the game.
