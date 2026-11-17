# File Encryption and Decryption Tool

A cryptographic desktop utility designed to securely encrypt and decrypt text files using **Caesar**, **Vigenère**, and **XOR** ciphers. Features a graphical user interface, file handling, input validation, and JDBC database audit logging.

---

## Key Features

1. **File Selection & Management**
   - Interactive file browser to select `.txt`, `.dat`, `.csv`, or `.log` files.
   - File metadata inspection (file name, path, formatted size in Bytes/KB/MB).
   - Direct text editing, file reloading, and workspace reset.
   - Export processed cryptographic output directly to file.

2. **Cryptographic Ciphers**
   - **Caesar Cipher**: Alphabetical shift cipher with integer key validation, circular modulo-26 wrapping, and negative shift support.
   - **Vigenère Cipher**: Polyalphabetic substitution cipher with alphabetic keyword validation.
   - **XOR Cipher**: Symmetric bitwise XOR cipher with Base64 encoding to prevent data corruption during text storage.

3. **Robust Exception Handling**
   - `FileNotFoundException` and `IOException` management for file reads and writes.
   - Custom `InvalidKeyException` with real-time requirement hints and validation dialogs.

4. **JDBC Operation History & Audit Trail**
   - Automatically records every encryption and decryption operation to the database table `operations`.
   - Stores: `id`, `timestamp`, `filename`, `cipher`, `mode` (`ENCRYPT`/`DECRYPT`), masked key, file size, and `status` (`SUCCESS`/`FAILED`).
   - Interactive History Viewer dialog with tabular display, refresh, and clear history capabilities.

5. **Modern Swing User Interface**
   - Clean, professional styling with native look and feel.
   - `BorderLayout` text presentation areas with line-wrap and real-time character/line counters.
   - `FlowLayout` action toolbars for streamlined workflow.
   - Dual-pane layout (Source / Preview on the left, Processed Output on the right).
   - "Transfer to Input" button for immediate decryption and testing.

---

## Project Structure

```
ManiProject/
├── bin/                             # Compiled Java class files
├── src/
│   └── filecrypt/
│       ├── algo/
│       │   ├── Cryptable.java       # Interface defining cryptographic operations
│       │   ├── Cipher.java          # Abstract class implementing Cryptable
│       │   ├── CaesarCipher.java    # Caesar Cipher implementation
│       │   ├── VigenereCipher.java  # Vigenere Cipher implementation
│       │   └── XorCipher.java       # Bitwise XOR Cipher implementation
│       ├── exception/
│       │   └── InvalidKeyException.java # Custom key validation exception
│       ├── util/
│       │   └── FileHandler.java     # File I/O and format utilities
│       ├── model/
│       │   └── OperationRecord.java # Data model for database operations
│       ├── db/
│       │   ├── DatabaseManager.java # JDBC connection and SQL query manager
│       │   └── EmbeddedJdbcDriver.java # Embedded JDBC driver (zero external JAR dependencies)
│       ├── ui/
│       │   ├── MainFrame.java       # Main desktop GUI window
│       │   ├── HistoryDialog.java   # Database history dialog with JTable
│       │   └── UITheme.java         # Color palette, fonts, and button styling
│       ├── test/
│       │   └── TestRunner.java      # Comprehensive automated unit tests
│       └── App.java                 # Package launcher
├── Main.java                        # Primary launcher
├── sample_document.txt              # Sample file for testing
├── compile.bat                      # One-click Windows compilation script
├── run.bat                          # One-click Windows launch script
└── README.md                        # Documentation
```

---

## How to Compile and Run

### Option 1: Using Batch Files (Windows)
1. Double-click `compile.bat` to compile the source code into `bin/`.
2. Double-click `run.bat` to launch the application.

### Option 2: Using the Command Line
```powershell
# Compile all files
javac -d bin -sourcepath ".;src" (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })

# Run the GUI application
java -cp bin Main

# Run automated tests
java -cp bin filecrypt.test.TestRunner
```

---

## Database Details

- **Table Name**: `operations`
- **Schema**:
  | Column | Type | Description |
  | :--- | :--- | :--- |
  | `id` | INTEGER | Auto-incrementing primary key |
  | `timestamp` | VARCHAR(30) | Date and time of operation |
  | `filename` | VARCHAR(255) | Name of processed file or direct input |
  | `cipher` | VARCHAR(50) | Algorithm used (Caesar, Vigenère, XOR) |
  | `mode` | VARCHAR(20) | `ENCRYPT` or `DECRYPT` |
  | `key_used` | VARCHAR(100) | Masked cryptographic key |
  | `file_size` | VARCHAR(50) | Formatted input size |
  | `status` | VARCHAR(20) | `SUCCESS` or `FAILED` |
