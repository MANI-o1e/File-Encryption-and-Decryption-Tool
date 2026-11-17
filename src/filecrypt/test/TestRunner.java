package filecrypt.test;

import filecrypt.algo.CaesarCipher;
import filecrypt.algo.VigenereCipher;
import filecrypt.algo.XorCipher;
import filecrypt.db.DatabaseManager;
import filecrypt.exception.InvalidKeyException;
import filecrypt.model.OperationRecord;
import filecrypt.util.FileHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Automated test suite validating all core requirements:
 * Unit 1: String methods, arrays, File class, FileHandler
 * Unit 2: Abstract Cipher, Cryptable interface, Caesar, Vigenere, XOR ciphers
 * Unit 3: FileNotFoundException, IOException, custom InvalidKeyException
 * Unit 4: JDBC database operations table creation, insertion, and retrieval
 */
public class TestRunner {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   FILE ENCRYPTION & DECRYPTION TOOL - TESTS      ");
        System.out.println("==================================================");

        testCaesarCipher();
        testVigenereCipher();
        testXorCipher();
        testInvalidKeyExceptions();
        testFileHandler();
        testDatabaseOperations();

        System.out.println("==================================================");
        System.out.printf("Test Results: %d Passed, %d Failed%n", testsPassed, testsFailed);
        System.out.println("==================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("[FAIL] " + testName);
            testsFailed++;
        }
    }

    private static void testCaesarCipher() {
        try {
            CaesarCipher caesar = new CaesarCipher();
            String plain = "Hello, World! 123";
            String key = "3";
            String encrypted = caesar.encrypt(plain, key);
            String decrypted = caesar.decrypt(encrypted, key);

            assertTrue("Caesar encrypts correctly", encrypted.equals("Khoor, Zruog! 123"));
            assertTrue("Caesar decrypts correctly", decrypted.equals(plain));

            // Negative shift
            String encNeg = caesar.encrypt("abc", "-1");
            assertTrue("Caesar handles negative shift", encNeg.equals("zab"));
            assertTrue("Caesar decrypts negative shift", caesar.decrypt(encNeg, "-1").equals("abc"));
        } catch (Exception e) {
            assertTrue("Caesar cipher threw unexpected exception: " + e.getMessage(), false);
        }
    }

    private static void testVigenereCipher() {
        try {
            VigenereCipher vigenere = new VigenereCipher();
            String plain = "ATTACK AT DAWN!";
            String key = "LEMON";
            String encrypted = vigenere.encrypt(plain, key);
            String decrypted = vigenere.decrypt(encrypted, key);

            assertTrue("Vigenere encrypts correctly", encrypted.equals("LXFOPV EF RNHR!"));
            assertTrue("Vigenere decrypts correctly", decrypted.equals(plain));
        } catch (Exception e) {
            assertTrue("Vigenere cipher threw unexpected exception: " + e.getMessage(), false);
        }
    }

    private static void testXorCipher() {
        try {
            XorCipher xor = new XorCipher();
            String plain = "Confidential Top Secret Data 2026!";
            String key = "StrongMasterKey@#99";
            String encrypted = xor.encrypt(plain, key);
            String decrypted = xor.decrypt(encrypted, key);

            assertTrue("XOR produces non-empty output", encrypted != null && !encrypted.isEmpty());
            assertTrue("XOR decrypts correctly back to original text", decrypted.equals(plain));
        } catch (Exception e) {
            assertTrue("Xor cipher threw unexpected exception: " + e.getMessage(), false);
        }
    }

    private static void testInvalidKeyExceptions() {
        CaesarCipher caesar = new CaesarCipher();
        boolean caughtCaesar = false;
        try {
            caesar.validateKey("not_a_number");
        } catch (InvalidKeyException e) {
            caughtCaesar = true;
        }
        assertTrue("Caesar catches invalid non-integer key", caughtCaesar);

        VigenereCipher vigenere = new VigenereCipher();
        boolean caughtVigenere = false;
        try {
            vigenere.validateKey("12345");
        } catch (InvalidKeyException e) {
            caughtVigenere = true;
        }
        assertTrue("Vigenere catches invalid non-alphabetic key", caughtVigenere);

        XorCipher xor = new XorCipher();
        boolean caughtXor = false;
        try {
            xor.validateKey("");
        } catch (InvalidKeyException e) {
            caughtXor = true;
        }
        assertTrue("Xor catches empty key", caughtXor);
    }

    private static void testFileHandler() {
        try {
            File tempTestFile = new File("test_sample.txt");
            String sampleContent = "Sample File Line 1\nLine 2 with special symbols: !@#$%^&*()";
            FileHandler.writeFile(tempTestFile, sampleContent);

            String readContent = FileHandler.readFile(tempTestFile);
            assertTrue("FileHandler reads what it writes", sampleContent.equals(readContent));

            // Test FileNotFoundException
            File nonExistent = new File("non_existent_file_abc_123_xyz.tmp");
            boolean caughtNotFound = false;
            try {
                FileHandler.readFile(nonExistent);
            } catch (FileNotFoundException e) {
                caughtNotFound = true;
            } catch (IOException e) {
                // Should be FileNotFoundException
            }
            assertTrue("FileHandler throws FileNotFoundException for missing file", caughtNotFound);

            // Clean up
            if (tempTestFile.exists()) {
                tempTestFile.delete();
            }
        } catch (Exception e) {
            assertTrue("FileHandler test threw unexpected error: " + e.getMessage(), false);
        }
    }

    private static void testDatabaseOperations() {
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            db.initTable();
            db.clearAllOperations(); // Start with a fresh table for deterministic testing

            OperationRecord record = new OperationRecord(
                    0,
                    "2026-11-17 12:00:00",
                    "test_document.txt",
                    "Caesar Cipher",
                    "ENCRYPT",
                    "3",
                    "1.5 KB",
                    "SUCCESS"
            );

            boolean logged = db.logOperation(record);
            assertTrue("JDBC logs operation into 'operations' table", logged);

            List<OperationRecord> records = db.getAllOperations();
            assertTrue("JDBC queries operation records via ResultSet", records != null && !records.isEmpty());

            OperationRecord latest = records.get(0);
            assertTrue("Logged record matches filename", "test_document.txt".equals(latest.getFileName()));
            assertTrue("Logged record matches cipher", "Caesar Cipher".equals(latest.getCipherType()));
            assertTrue("Logged record matches status", "SUCCESS".equals(latest.getStatus()));

        } catch (Exception e) {
            assertTrue("Database test failed: " + e.getMessage(), false);
        }
    }
}
