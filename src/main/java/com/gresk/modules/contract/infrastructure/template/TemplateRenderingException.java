package com.gresk.modules.contract.infrastructure.template;

/** Fallo técnico del pipeline de renderizado (no es un error de dominio). */
public class TemplateRenderingException extends RuntimeException {
    public TemplateRenderingException(String message, Throwable cause) {
        super(message, cause);
    }
}
