package filecrypt.algo;

import filecrypt.exception.InvalidKeyException;

/**
 * Caesar Cipher implementation.
 * Shifts letters along the alphabet by an integer offset.
 */
public class CaesarCipher extends Cipher {

    public CaesarCipher() {
        super("Caesar Cipher", "Integer shift value (e.g., 3, 7, -4)");
    }

    @Override
    public void validateKey(String key) throws InvalidKeyException {
        ensureNonEmptyKey(key);
        try {
            Integer.parseInt(key.trim());
        } catch (NumberFormatException e) {
            throw new InvalidKeyException("Caesar Cipher key must be a valid integer. Received: '" + key + "'", e);
        }
    }

    private int parseShift(String key) throws InvalidKeyException {
        validateKey(key);
        int shift = Integer.parseInt(key.trim()) % 26;
        if (shift < 0) {
            shift += 26;
        }
        return shift;
    }

    @Override
    public String encrypt(String plainText, String key) throws InvalidKeyException {
        if (plainText == null) {
            return "";
        }
        int shift = parseShift(key);
        char[] characters = plainText.toCharArray();
        char[] result = new char[characters.length];

        for (int i = 0; i < characters.length; i++) {
            char ch = characters[i];
            if (ch >= 'A' && ch <= 'Z') {
                result[i] = (char) ('A' + (ch - 'A' + shift) % 26);
            } else if (ch >= 'a' && ch <= 'z') {
                result[i] = (char) ('a' + (ch - 'a' + shift) % 26);
            } else {
                result[i] = ch; // Keep punctuation, spaces, numbers unchanged
            }
        }
        return new String(result);
    }

    @Override
    public String decrypt(String cipherText, String key) throws InvalidKeyException {
        if (cipherText == null) {
            return "";
        }
        int shift = parseShift(key);
        int reverseShift = (26 - shift) % 26;
        
        char[] characters = cipherText.toCharArray();
        char[] result = new char[characters.length];

        for (int i = 0; i < characters.length; i++) {
            char ch = characters[i];
            if (ch >= 'A' && ch <= 'Z') {
                result[i] = (char) ('A' + (ch - 'A' + reverseShift) % 26);
            } else if (ch >= 'a' && ch <= 'z') {
                result[i] = (char) ('a' + (ch - 'a' + reverseShift) % 26);
            } else {
                result[i] = ch;
            }
        }
        return new String(result);
    }
}
