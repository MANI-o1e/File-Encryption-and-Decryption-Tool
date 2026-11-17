package filecrypt.algo;

import filecrypt.exception.InvalidKeyException;

/**
 * Vigenere Cipher implementation.
 * Polyalphabetic substitution cipher based on a keyword.
 */
public class VigenereCipher extends Cipher {

    public VigenereCipher() {
        super("Vigenere Cipher", "Alphabetic keyword (e.g., SECRET, KEY, CIPHER)");
    }

    @Override
    public void validateKey(String key) throws InvalidKeyException {
        ensureNonEmptyKey(key);
        String cleaned = key.trim();
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (!Character.isLetter(c)) {
                throw new InvalidKeyException("Vigenere Cipher key must contain only alphabetic letters (A-Z, a-z). Invalid character: '" + c + "'");
            }
        }
    }

    private String getNormalizedKey(String key) throws InvalidKeyException {
        validateKey(key);
        return key.trim().toUpperCase();
    }

    @Override
    public String encrypt(String plainText, String key) throws InvalidKeyException {
        if (plainText == null) {
            return "";
        }
        String normalizedKey = getNormalizedKey(key);
        char[] chars = plainText.toCharArray();
        char[] result = new char[chars.length];
        int keyIndex = 0;
        int keyLength = normalizedKey.length();

        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch >= 'A' && ch <= 'Z') {
                int shift = normalizedKey.charAt(keyIndex % keyLength) - 'A';
                result[i] = (char) ('A' + (ch - 'A' + shift) % 26);
                keyIndex++;
            } else if (ch >= 'a' && ch <= 'z') {
                int shift = normalizedKey.charAt(keyIndex % keyLength) - 'A';
                result[i] = (char) ('a' + (ch - 'a' + shift) % 26);
                keyIndex++;
            } else {
                result[i] = ch;
            }
        }
        return new String(result);
    }

    @Override
    public String decrypt(String cipherText, String key) throws InvalidKeyException {
        if (cipherText == null) {
            return "";
        }
        String normalizedKey = getNormalizedKey(key);
        char[] chars = cipherText.toCharArray();
        char[] result = new char[chars.length];
        int keyIndex = 0;
        int keyLength = normalizedKey.length();

        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch >= 'A' && ch <= 'Z') {
                int shift = normalizedKey.charAt(keyIndex % keyLength) - 'A';
                result[i] = (char) ('A' + (ch - 'A' - shift + 26) % 26);
                keyIndex++;
            } else if (ch >= 'a' && ch <= 'z') {
                int shift = normalizedKey.charAt(keyIndex % keyLength) - 'A';
                result[i] = (char) ('a' + (ch - 'a' - shift + 26) % 26);
                keyIndex++;
            } else {
                result[i] = ch;
            }
        }
        return new String(result);
    }
}
