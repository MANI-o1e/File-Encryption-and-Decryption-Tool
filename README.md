# File Encryption and Decryption Tool

A cryptographic desktop application designed to encrypt and decrypt text files using **Caesar**, **Vigenère**, and **XOR** ciphers. Built with Java Swing, object-oriented principles, modular architecture, and JDBC database audit logging.

---

## Project Overview

- **Project Title:** File Encryption and Decryption Tool
- **Description:** A tool to encrypt and decrypt text files using Caesar, Vigenère, and XOR ciphers.
- **Group:** Group 2

### Team Members

| Role | Name | UID |
| :--- | :--- | :--- |
| **Leader** | Gurubandhan Sharma | 2510042044 |
| **Member 1** | Manmeet Singh | 2510042075 |
| **Member 2** | Mani | 2510042060 |
| **Member 3** | Jishan Sankar | 115329 |

---

## Project Modules

| # | Module | Description |
| :---: | :--- | :--- |
| **1** | **File Selection** | Interactive file chooser for `.txt`, `.dat`, `.csv`, `.log` files with file details, content preview, reload, and export. |
| **2** | **Caesar Cipher** | Character shifting cipher with integer key validation, circular modulo-26 wrapping, and negative shift support. |
| **3** | **Vigenère Cipher** | Polyalphabetic substitution cipher based on an alphabetic keyword. |
| **4** | **XOR Cipher** | Symmetric bitwise XOR cipher safely wrapped with Base64 encoding to prevent character corruption during text file storage. |
| **5** | **Operation History** | Persistent JDBC database logging into the `operations` table with an interactive audit history table dialog. |

---

## Syllabus Concepts Applied

| Unit | Concepts Implemented | Code Location |
| :--- | :--- | :--- |
| **Unit 1** | Classes `Cipher`, `FileHandler`; `String` methods; arrays (`char[]`, `byte[]`); `File` class | `filecrypt.algo.Cipher`<br>`filecrypt.util.FileHandler` |
| **Unit 2** | Abstract class `Cipher -> Caesar/Vigenere/Xor` with method overriding; interface `Cryptable`; package `filecrypt.algo` | `filecrypt.algo.*` |
| **Unit 3** | `FileNotFoundException` and `IOException` handling; custom `InvalidKeyException` | `filecrypt.exception.InvalidKeyException`<br>`filecrypt.util.FileHandler` |
| **Unit 4** | JDBC to store operation history; Database Table: `operations` | `filecrypt.db.DatabaseManager`<br>`filecrypt.db.EmbeddedJdbcDriver` |
| **Unit 5** | Swing `FlowLayout` buttons; `BorderLayout` text presentation areas; event listeners | `filecrypt.ui.MainFrame`<br>`filecrypt.ui.HistoryDialog` |

---

## Key Features

1. **Clean & High-Contrast User Interface**
   - Clean, professional styling with zero clutter.
   - High-contrast color palette (Royal Blue, Emerald Green, Slate) ensuring maximum legibility on all monitors.
   - Anti-aliased buttons with bold, crystal-clear labels.
   - Structured 2-row configuration layout preventing any overlap between key hints and action buttons.

2. **Dual-Pane Text Workspace**
   - **Source / File Preview Area** (Left): Displays loaded file contents or pasted text, with live character and line counters.
   - **Processed Output Area** (Right): Displays real-time cryptographic output with quick action buttons to **Copy**, **Save to File**, or **Transfer to Input**.

3. **Zero-Dependency JDBC Architecture**
   - Built-in pure-Java embedded JDBC driver compliant with `java.sql.*` standard interfaces (`Connection`, `Statement`, `PreparedStatement`, `ResultSet`).
   - Automatically detects and supports external SQLite/H2 JDBC drivers if present on the classpath.
   - Runs out of the box on any standard Java Runtime Environment without requiring external database installations or third-party JARs.

4. **Automated Testing Suite**
   - 18 automated unit and integration tests covering cipher algorithms, key validations, file handling, exception handling, and JDBC database operations.

---

## Project Structure
