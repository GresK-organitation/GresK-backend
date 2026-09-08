package com.gresk.modules.contract.domain.service;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentHasherTest {

    @Test
    void calculaElHashSha256Conocido() {
        // sha256("hello world") = b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9
        String hash = DocumentHasher.sha256Hex("hello world".getBytes(StandardCharsets.UTF_8));
        assertEquals("b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9", hash);
    }

    @Test
    void hashesDiferentesParaContenidosDiferentes() {
        String h1 = DocumentHasher.sha256Hex("a".getBytes(StandardCharsets.UTF_8));
        String h2 = DocumentHasher.sha256Hex("b".getBytes(StandardCharsets.UTF_8));
        org.junit.jupiter.api.Assertions.assertNotEquals(h1, h2);
    }
}
