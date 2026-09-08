package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.application.dto.ResolvedEpkDownload;
import com.gresk.modules.artist.application.port.in.ResolvePublicEpkDownloadPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/epk")
@RequiredArgsConstructor
public class EpkPublicController {

    private final ResolvePublicEpkDownloadPort resolveDownloadUseCase;

    // Redirige (302) a la URL resuelta del fichero en Cloudinary — pensado para
    // compartirse directamente por email/WhatsApp.
    @GetMapping("/{token}")
    public ResponseEntity<Void> download(@PathVariable String token) {
        ResolvedEpkDownload resolved = resolveDownloadUseCase.execute(token);
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, resolved.url())
                .build();
    }
}
