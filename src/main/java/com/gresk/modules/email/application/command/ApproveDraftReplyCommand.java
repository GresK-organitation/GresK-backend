package com.gresk.modules.email.application.command;

import java.util.UUID;

/** {@code editedBody} es opcional: si viene, sustituye al cuerpo generado por la IA. */
public record ApproveDraftReplyCommand(
        UUID draftId,
        UUID promoterId,
        String editedBody
) {}
