package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.dto.ResolvedEpkDownload;

/** Endpoint público (sin autenticación de promotora): el token ES la autorización. */
public interface ResolvePublicEpkDownloadPort {
    ResolvedEpkDownload execute(String token);
}
