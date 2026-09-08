package com.gresk.modules.artist.application.port.in;

/** Invocado por el scheduler diario; genera DocumentExpiryAlert para documentos
 *  próximos a caducar y notifica por email. */
public interface CheckDocumentExpiryPort {
    void execute();
}
