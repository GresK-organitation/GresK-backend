package com.gresk.modules.email.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cifrado AES-256-GCM para los tokens OAuth de Gmail en reposo.
 * Formato del cifrado: base64(iv[12] || ciphertext+tag).
 *
 * La clave se inyecta en base64 (generar con: openssl rand -base64 32).
 * Si no está configurada se genera una efímera con WARN: los tokens
 * cifrados con ella no sobrevivirán a un reinicio (solo aceptable en dev).
 */
@Slf4j
@Component
public class TokenEncryptionService {

    private static final String TRANSFORMATION  = "AES/GCM/NoPadding";
    private static final int    IV_LENGTH_BYTES = 12;
    private static final int    TAG_LENGTH_BITS = 128;

    private final SecretKey    key;
    private final SecureRandom random = new SecureRandom();

    public TokenEncryptionService(@Value("${gresk.email.encryption.key:}") String base64Key) {
        this.key = base64Key != null && !base64Key.isBlank()
                ? fromBase64(base64Key)
                : ephemeralKey();
    }

    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            buffer.put(iv).put(ciphertext);
            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (Exception e) {
            throw new IllegalStateException("Token encryption failed", e);
        }
    }

    public String decrypt(String encoded) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(encoded));
            byte[] iv = new byte[IV_LENGTH_BYTES];
            buffer.get(iv);
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            return new String(cipher.doFinal(ciphertext));
        } catch (Exception e) {
            throw new IllegalStateException("Token decryption failed", e);
        }
    }

    private SecretKey fromBase64(String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException(
                    "gresk.email.encryption.key must be a 256-bit key in base64 (got "
                            + keyBytes.length * 8 + " bits). Generate with: openssl rand -base64 32");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    private SecretKey ephemeralKey() {
        log.warn("gresk.email.encryption.key is not set — using an EPHEMERAL key. "
                + "Stored Gmail tokens will be unreadable after restart. "
                + "Set EMAIL_TOKEN_ENCRYPTION_KEY in production (openssl rand -base64 32).");
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            return generator.generateKey();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot generate AES key", e);
        }
    }
}
