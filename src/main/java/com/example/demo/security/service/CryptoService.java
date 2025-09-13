package com.example.demo.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Service for performing symmetric encryption and decryption.
 * This class uses a single, shared key to encrypt and decrypt data.
 * It is now using a random salt for each encryption to ensure that the
 * ciphertext is unique for every operation, improving security.
 */
@Service
public class CryptoService {
    private static final Logger logger = LoggerFactory.getLogger(CryptoService.class);
    private final String secretKey;

    /**
     * Constructs the service with a secret key from properties.
     * @param secretKey The key for encryption/decryption, loaded from properties.
     */
    public CryptoService(@Value("${application.security.symmetric.key}") String secretKey) {
        this.secretKey = secretKey;
        logger.info("Crypto service initialized with secret key.");
    }

    /**
     * Encrypts a given plain text string.
     * The output string will contain the salt and the encrypted data,
     * separated by a colon, e.g., "salt:ciphertext".
     *
     * @param plainText The string to encrypt.
     * @return The encrypted string with the prepended salt.
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return null;
        }
        try {
            // Generate a unique, random salt for each encryption.
            SecureRandom random = new SecureRandom();
            byte[] saltBytes = new byte[8];
            random.nextBytes(saltBytes);
            String salt = HexFormat.of().formatHex(saltBytes);

            TextEncryptor encryptor = Encryptors.text(secretKey, salt);
            String encryptedText = encryptor.encrypt(plainText);
            
            // Combine the salt and the encrypted text.
            return salt + ":" + encryptedText;
        } catch (Exception e) {
            logger.error("Failed to encrypt data. {}", e.getMessage());
            return null;
        }
    }

    /**
     * Decrypts an encrypted text string.
     * The input string is expected to be in the "salt:ciphertext" format.
     *
     * @param encryptedText The string to decrypt.
     * @return The decrypted plain text string.
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || !encryptedText.contains(":")) {
            logger.error("Invalid encrypted string format.");
            return null;
        }

        try {
            // Extract the salt and the ciphertext from the input string.
            String[] parts = encryptedText.split(":", 2);
            String salt = parts[0];
            String cipherText = parts[1];

            TextEncryptor decryptor = Encryptors.text(secretKey, salt);
            return decryptor.decrypt(cipherText);
        } catch (Exception e) {
            logger.error("Failed to decrypt data. {}", e.getMessage());
            return null;
        }
    }
}
