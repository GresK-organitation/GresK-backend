package com.gresk.modules.email.application.event;

import java.util.UUID;

/** Se publica tras persistir un correo entrante; dispara el procesamiento asíncrono. */
public record EmailReceivedEvent(UUID emailId) {}
