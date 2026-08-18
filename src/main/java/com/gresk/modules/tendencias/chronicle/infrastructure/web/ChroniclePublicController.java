package com.gresk.modules.tendencias.chronicle.infrastructure.web;

import com.gresk.modules.tendencias.chronicle.application.dto.ChronicleResponse;
import com.gresk.modules.tendencias.chronicle.application.dto.ChronicleResponseMapper;
import com.gresk.modules.tendencias.chronicle.application.usecase.ListPublishedChroniclesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tendencias/chronicles")
@RequiredArgsConstructor
public class ChroniclePublicController {

    private final ListPublishedChroniclesUseCase listPublishedChronicles;

    @GetMapping
    public ResponseEntity<List<ChronicleResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var chronicles = listPublishedChronicles.execute(page, size).stream()
                .map(ChronicleResponseMapper::toResponse)
                .toList();
        return ResponseEntity.ok(chronicles);
    }
}
