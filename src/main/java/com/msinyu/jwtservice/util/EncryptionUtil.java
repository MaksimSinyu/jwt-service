package com.msinyu.jwtservice.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Utility class for encryption and decryption operations.
 */
@Component
public class EncryptionUtil {

    private static final String AES = "AES";
    private static final String AES_GCM_NOPADDING = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    private SecretKey secretKey;

    public EncryptionUtil() throws Exception {
        //TODO from a key store
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        keyGen.init(256); // - For AES-256
        secretKey = keyGen.generateKey();
    }

    /**
     * Encrypts the given plaintext using AES-GCM.
     *
     * @param plaintext The plaintext to encrypt.
     * @return Encrypted data as a Base64 encoded string.
     * @throws Exception If encryption fails.
     */
    public String encrypt(String plaintext) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        byte[] encryptedWithIv = new byte[IV_LENGTH_BYTE + encrypted.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, IV_LENGTH_BYTE);
        System.arraycopy(encrypted, 0, encryptedWithIv, IV_LENGTH_BYTE, encrypted.length);

        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }

    /**
     * Decrypts the given ciphertext using AES-GCM.
     *
     * @param ciphertext The Base64 encoded ciphertext to decrypt.
     * @return Decrypted plaintext.
     * @throws Exception If decryption fails.
     */
    public String decrypt(String ciphertext) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(ciphertext);
        byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH_BYTE);
        byte[] encrypted = Arrays.copyOfRange(decoded, IV_LENGTH_BYTE, decoded.length);

        Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
