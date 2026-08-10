package com.gresk.modules.email.infrastructure.security;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class TokenEncryptionServiceTest {

    private static String randomKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    @Test
    void encryptYDecryptSonInversas() {
        TokenEncryptionService service = new TokenEncryptionService(randomKey());
        String token = "ya29.a0AfH6SMBx-gmail-access-token-de-prueba";

        String encrypted = service.encrypt(token);

        assertNotEquals(token, encrypted);
        assertEquals(token, service.decrypt(encrypted));
    }

    @Test
    void dosCifradosDelMismoTextoSonDistintos_ivAleatorio() {
        TokenEncryptionService service = new TokenEncryptionService(randomKey());

        String first  = service.encrypt("mismo-token");
        String second = service.encrypt("mismo-token");

        assertNotEquals(first, second);
        assertEquals(service.decrypt(first), service.decrypt(second));
    }

    @Test
    void descifrarConOtraClaveFalla() {
        String encrypted = new TokenEncryptionService(randomKey()).encrypt("token-secreto");
        TokenEncryptionService otherKey = new TokenEncryptionService(randomKey());

        assertThrows(IllegalStateException.class, () -> otherKey.decrypt(encrypted));
    }

    @Test
    void unaClaveQueNoEsDe256BitsSeRechaza() {
        String shortKey = Base64.getEncoder().encodeToString(new byte[16]);

        assertThrows(IllegalArgumentException.class, () -> new TokenEncryptionService(shortKey));
    }

    @Test
    void sinClaveConfigurada_usaUnaEfimeraQueFuncionaEnLaMismaInstancia() {
        TokenEncryptionService service = new TokenEncryptionService("");

        assertEquals("token", service.decrypt(service.encrypt("token")));
    }
}
