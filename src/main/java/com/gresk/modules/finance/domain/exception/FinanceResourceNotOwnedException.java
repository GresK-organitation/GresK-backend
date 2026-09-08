package com.gresk.modules.finance.domain.exception;

/** Excepción genérica de propiedad para los agregados del módulo finance (evita duplicar la misma excepción por agregado). */
public class FinanceResourceNotOwnedException extends RuntimeException {
    public FinanceResourceNotOwnedException() {
        super("You do not have permission to access this financial resource");
    }
}
