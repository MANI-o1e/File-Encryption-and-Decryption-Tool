package filecrypt.algo;

import filecrypt.exception.InvalidKeyException;

/**
 * Abstract base class for all cipher implementations.
 * Implements Cryptable and provides common algorithm metadata and string/array utilities.
 */
public abstract class Cipher implements Cryptable {

    protected final String algorithmName;
    protected final String keyRequirementDescription;

    public Cipher(String algorithmName, String keyRequirementDescription) {
        this.algorithmName = algorithmName;
        this.keyRequirementDescription = keyRequirementDescription;
    }

    @Override
    public String getAlgorithmName() {
        return algorithmName;
    }

    public String getKeyRequirementDescription() {
        return keyRequirementDescription;
    }

    /**
     * Common validation helper for checking non-null and non-empty keys.
     */
    protected void ensureNonEmptyKey(String key) throws InvalidKeyException {
        if (key == null || key.trim().isEmpty()) {
            throw new InvalidKeyException("Key cannot be empty or blank for " + algorithmName + ".");
        }
    }

    @Override
    public abstract String encrypt(String plainText, String key) throws InvalidKeyException;

    @Override
    public abstract String decrypt(String cipherText, String key) throws InvalidKeyException;

    @Override
    public abstract void validateKey(String key) throws InvalidKeyException;

    @Override
    public String toString() {
        return algorithmName;
    }
}
