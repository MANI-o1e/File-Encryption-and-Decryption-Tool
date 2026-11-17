# 🔐 File Encryption and Decryption Tool

**A Java-Based Cryptographic Desktop Application**

A secure, modular, and user-friendly desktop application designed to encrypt and decrypt text files using three classical cryptographic algorithms: **Caesar Cipher, Vigenère Cipher, and XOR Cipher**.

Developed using **Java Swing, Object-Oriented Programming (OOP), and JDBC**, this project demonstrates fundamental cryptographic concepts alongside practical desktop application development and database management.

---

## 📌 Table of Contents

- [Project Overview](#-project-overview)
- [Team Members](#-team-members)
- [Key Features](#-key-features)
- [Encryption Algorithms](#-encryption-algorithms)
- [Project Modules](#-project-modules)
- [Technology Stack](#-technology-stack)
- [Project Architecture](#-project-architecture)
- [Project Structure](#-project-structure)
- [Syllabus Concepts Applied](#-syllabus-concepts-applied)
- [Installation and Setup](#-installation-and-setup)
- [How to Use](#-how-to-use)
- [Database and Audit Logging](#-database-and-audit-logging)
- [Testing](#-testing)
- [Security Considerations](#-security-considerations)
- [Future Enhancements](#-future-enhancements)
- [Contributors](#-contributors)

---

## 📖 Project Overview

| Detail | Information |
|---|---|
| **Project Title** | File Encryption and Decryption Tool |
| **Project Type** | Java Desktop Application |
| **Group** | Group 2 |
| **Programming Language** | Java |
| **GUI Framework** | Java Swing |
| **Database Technology** | JDBC |
| **Architecture** | Modular, Object-Oriented Architecture |
| **Supported Algorithms** | Caesar, Vigenère, XOR |

### 🎯 Objective

The primary objective of this project is to develop a desktop-based application that demonstrates how encryption and decryption work through different cryptographic techniques.

The application allows users to load text files, select an encryption algorithm, provide the appropriate key, and generate encrypted or decrypted output.

It also maintains a persistent record of cryptographic operations using JDBC-based database logging.

---

## 👥 Team Members

**Group 2**

| Role | Name | UID |
|---|---|---|
| 👑 Team Leader | Gurubandhan Sharma | 2510042044 |
| Member 1 | Manmeet Singh | 2510042075 |
| Member 2 | Mani | 2510042060 |
| Member 3 | Jishan Sankar | 115329 |

---

## ✨ Key Features

### 1. File Management
- Interactive file selection using Java JFileChooser.
- Supports `.txt`, `.dat`, `.csv`, and `.log` files.
- Displays loaded file contents in the source panel.
- Provides file information and content preview.
- Supports reloading and exporting files.

### 2. Multiple Encryption Algorithms
- Caesar Cipher with customizable integer keys.
- Vigenère Cipher using alphabetic keywords.
- XOR Cipher using symmetric bitwise operations.
- Supports both encryption and decryption operations.

### 3. Dual-Pane Workspace
- **Source Panel:** Displays original file contents or manually entered text.
- **Output Panel:** Displays encrypted or decrypted results.
- Live character and line counters.
- Copy output to clipboard.
- Save processed output to a file.
- Transfer output directly back to the input panel.

### 4. Database Audit Logging
- Records encryption and decryption operations.
- Stores operation details through JDBC.
- Maintains persistent operation history.
- Provides an interactive history table for reviewing previous operations.

### 5. Professional Graphical Interface
- Built using Java Swing.
- High-contrast Royal Blue, Emerald Green, and Slate color scheme.
- Organized interface with clear action buttons.
- Structured layouts for improved readability.
- Event-driven interaction using Java listeners.

### 6. Automated Testing
- 18 automated unit and integration tests.
- Tests encryption and decryption algorithms.
- Validates cryptographic keys.
- Tests file handling and exception management.
- Verifies JDBC database operations.

---

## 🔑 Encryption Algorithms

The application implements three encryption algorithms.

### 1. Caesar Cipher

A classical substitution cipher that encrypts text by shifting alphabetic characters by a fixed number of positions.

**Example:**

| Parameter | Value |
|---|---|
| Original Text | HELLO |
| Encryption Key | 3 |
| Encrypted Text | KHOOR |

**Formula:**

Encryption:

`E(x) = (x + k) mod 26`

Decryption:

`D(x) = (x - k + 26) mod 26`

**Features:**
- Integer-based encryption key.
- Circular alphabet shifting.
- Supports negative shifts.
- Preserves non-alphabetic characters.

### 2. Vigenère Cipher

A polyalphabetic substitution cipher that uses a keyword to determine different character shifts throughout the message.

**Example:**

| Parameter | Value |
|---|---|
| Original Text | HELLO |
| Encryption Key | KEY |
| Encrypted Text | RIJVS |

**Formula:**

Encryption:

`Cᵢ = (Pᵢ + Kᵢ) mod 26`

Decryption:

`Pᵢ = (Cᵢ - Kᵢ + 26) mod 26`

**Features:**
- Uses alphabetic keywords.
- Repeating keyword-based encryption.
- Validates keyword input.
- Supports encryption and decryption.

### 3. XOR Cipher

A symmetric encryption technique that applies the bitwise XOR operation between input data and a key.

**Formula:**

Encryption:

`C = P XOR K`

Decryption:

`P = C XOR K`

**Features:**
- Uses bitwise XOR operations.
- Same key is used for encryption and decryption.
- Converts encrypted bytes into Base64 for safe text storage.
- Supports binary-safe processing.

---

## 🧩 Project Modules

| # | Module | Description |
|---|---|---|
| 01 | File Selection | Loads, previews, reloads, and exports supported text files. |
| 02 | Caesar Cipher | Implements character shifting with integer key validation and modulo-26 wrapping. |
| 03 | Vigenère Cipher | Implements keyword-based polyalphabetic encryption. |
| 04 | XOR Cipher | Implements symmetric bitwise encryption with Base64 encoding. |
| 05 | Operation History | Stores and displays operation records using JDBC. |

---

## 🛠 Technology Stack

| Technology | Purpose |
|---|---|
| Java | Core application development |
| Java Swing | Graphical user interface |
| AWT | Layout management and event handling |
| JDBC | Database connectivity |
| SQL | Operation history management |
| Base64 | Encoding encrypted XOR output |
| JUnit / Testing Framework | Automated testing |

---

## 🏗 Project Architecture

The application follows a modular, object-oriented architecture that separates the user interface, encryption logic, file handling, database operations, and exception management.

**Architecture Overview:**

```text
             USER
               |
               v
       +----------------+
       |  Java Swing UI |
       |   MainFrame    |
       +----------------+
               |
       +-------+--------+
       |                |
       v                v
+-------------+  +----------------+
| FileHandler |  | Cipher Manager |
+-------------+  +----------------+
                         |
              +----------+----------+
              |          |          |
              v          v          v
          +--------+ +---------+ +--------+
          | Caesar | |Vigenere | |  XOR   |
          +--------+ +---------+ +--------+
                         |
                         v
                +----------------+
                | Output Handler |
                +----------------+
                         |
                         v
                +----------------+
                | DatabaseManager|
                +----------------+
                         |
                         v
                +----------------+
                | JDBC Database  |
                |  Operations    |
                +----------------+
```

---

## 📂 Project Structure

```text
File-Encryption-Decryption-Tool/
│
├── src/
│   └── filecrypt/
│       │
│       ├── algo/
│       │   ├── Cipher.java
│       │   ├── Cryptable.java
│       │   ├── CaesarCipher.java
│       │   ├── VigenereCipher.java
│       │   └── XorCipher.java
│       │
│       ├── db/
│       │   ├── DatabaseManager.java
│       │   └── EmbeddedJdbcDriver.java
│       │
│       ├── exception/
│       │   └── InvalidKeyException.java
│       │
│       ├── ui/
│       │   ├── MainFrame.java
│       │   └── HistoryDialog.java
│       │
│       └── util/
│           └── FileHandler.java
│
├── test/
│   └── [Automated test files]
│
├── README.md
└── [Build configuration files]
```

*Note: The structure above represents the documented project architecture. Update filenames and directories if your actual source code differs.*

---

## 📚 Syllabus Concepts Applied

This project integrates practical programming concepts from five academic units.

| Unit | Concepts Implemented | Code Location |
|---|---|---|
| Unit 1 | Classes, objects, String methods, arrays, File class | `filecrypt.algo.Cipher`, `filecrypt.util.FileHandler` |
| Unit 2 | Abstract classes, inheritance, interfaces, method overriding, packages | `filecrypt.algo.*` |
| Unit 3 | Exception handling, IOException, FileNotFoundException, custom exceptions | `filecrypt.exception.InvalidKeyException`, `filecrypt.util.FileHandler` |
| Unit 4 | JDBC, database connectivity, SQL operations, persistent history | `filecrypt.db.DatabaseManager` |
| Unit 5 | Java Swing, FlowLayout, BorderLayout, event listeners | `filecrypt.ui.Main
