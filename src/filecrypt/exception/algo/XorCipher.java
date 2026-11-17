package filecrypt.algo;

import filecrypt.exception.InvalidKeyException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * XOR Cipher implementation.
 * Performs bitwise XOR between data and a repeating key.
 * Encrypted data is safely encoded in Base64 for file/text storage without data loss.
 */
public class XorCipher extends Cipher {

    public XorCipher() {
        super("XOR Cipher", "Any non-empty text or passphrase (e.g., mySecretKey123)");
    }

    @Override
    public void validateKey(String key) throws InvalidKeyException {
        ensureNonEmptyKey(key);
        if (key.length() < 1) {
            throw new InvalidKeyException("XOR Cipher key must have at least 1 character.");
        }
    }

    @Override
    public String encrypt(String plainText, String key) throws InvalidKeyException {
        if (plainText == null) {
            return "";
        }
        validateKey(key);
        byte[] inputBytes = plainText.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] xorResult = new byte[inputBytes.length];

        for (int i = 0; i < inputBytes.length; i++) {
            xorResult[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        // Return Base64 encoded string so arbitrary binary bytes can be safely saved as text
        return Base64.getEncoder().encodeToString(xorResult);
    }

    @Override
    public String decrypt(String cipherText, String key) throws InvalidKeyException {
        if (cipherText == null || cipherText.isEmpty()) {
            return "";
        }
        validateKey(key);
        byte[] encryptedBytes;
        try {
            // Attempt Base64 decode first
            encryptedBytes = Base64.getDecoder().decode(cipherText.trim());
        } catch (IllegalArgumentException e) {
            // If raw text was passed instead of Base64, fall back to UTF-8 bytes
            encryptedBytes = cipherText.getBytes(StandardCharsets.UTF_8);
        }

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] plainBytes = new byte[encryptedBytes.length];

        for (int i = 0; i < encryptedBytes.length; i++) {
            plainBytes[i] = (byte) (encryptedBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return new String(plainBytes, StandardCharsets.UTF_8);
    }
}
