package filecrypt.exception;

/**
 * Custom exception thrown when an encryption or decryption key
 * is missing, malformed, or fails validation for a specific cipher.
 */
public class InvalidKeyException extends Exception {
    public InvalidKeyException(String message) {
        super(message);
    }

    public InvalidKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
