package com.gresk.modules.email.infrastructure.web.dto;

/** {@code editedBody} es opcional: si viene, sustituye al cuerpo antes de enviar. */
public record ApproveDraftRequest(String editedBody) {}
