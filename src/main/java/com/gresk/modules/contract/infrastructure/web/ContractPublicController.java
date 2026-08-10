package com.gresk.modules.contract.infrastructure.web;

import com.gresk.modules.contract.application.usecase.GetPublicContractUseCase;
import com.gresk.modules.contract.infrastructure.web.dto.ContractResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractPublicController {

    private final GetPublicContractUseCase getPublicUseCase;
    private final ContractResponseMapper  mapper;

    @GetMapping("/public/{shareToken}")
    public ResponseEntity<ContractResponse> getPublic(@PathVariable String shareToken) {
        return ResponseEntity.ok(mapper.toResponse(getPublicUseCase.execute(shareToken)));
    }
}
