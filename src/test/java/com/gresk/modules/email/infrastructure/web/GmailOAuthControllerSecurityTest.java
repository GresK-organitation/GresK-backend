package com.gresk.modules.email.infrastructure.web;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Garantiza la política de acceso del flujo OAuth: connect y disconnect
 * exigen rol PROMOTER; el callback (redirección del navegador desde Google,
 * sin JWT) queda fuera de la autorización por rol y se protege con el
 * state anti-CSRF.
 */
class GmailOAuthControllerSecurityTest {

    @Test
    void connectSoloEsAccesibleConRolPromoter() throws Exception {
        assertRequiresPromoterRole(GmailOAuthController.class.getMethod("connect", String.class));
    }

    @Test
    void disconnectSoloEsAccesibleConRolPromoter() throws Exception {
        assertRequiresPromoterRole(GmailOAuthController.class.getMethod("disconnect", String.class));
    }

    @Test
    void elCallbackNoExigeRol_loProtegeElStateDeUnSoloUso() throws Exception {
        Method callback = GmailOAuthController.class.getMethod("callback", String.class, String.class);
        assertNull(callback.getAnnotation(PreAuthorize.class));
        assertNull(GmailOAuthController.class.getAnnotation(PreAuthorize.class),
                "un @PreAuthorize a nivel de clase bloquearía el callback de Google");
    }

    private void assertRequiresPromoterRole(Method method) {
        PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
        assertNotNull(annotation, method.getName() + " debe estar protegido con @PreAuthorize");
        assertTrue(annotation.value().contains("hasRole('PROMOTER')"),
                method.getName() + " debe exigir el rol PROMOTER");
    }
}
