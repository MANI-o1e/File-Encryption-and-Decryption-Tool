package filecrypt.algo;

import filecrypt.exception.InvalidKeyException;

/**
 * Interface defining the cryptographic operations for text ciphers.
 */
public interface Cryptable {
    /**
     * Encrypts the input plain text using the specified key.
     *
     * @param plainText the text to encrypt
     * @param key the encryption key
     * @return the encrypted cipher text
     * @throws InvalidKeyException if the key is invalid
     */
    String encrypt(String plainText, String key) throws InvalidKeyException;

    /**
     * Decrypts the input cipher text using the specified key.
     *
     * @param cipherText the text to decrypt
     * @param key the decryption key
     * @return the decrypted plain text
     * @throws InvalidKeyException if the key is invalid
     */
    String decrypt(String cipherText, String key) throws InvalidKeyException;

    /**
     * Validates whether the given key is suitable for this cipher.
     *
     * @param key the key to validate
     * @throws InvalidKeyException if the key fails validation
     */
    void validateKey(String key) throws InvalidKeyException;

    /**
     * Returns the human-readable algorithm name.
     */
    String getAlgorithmName();
}
