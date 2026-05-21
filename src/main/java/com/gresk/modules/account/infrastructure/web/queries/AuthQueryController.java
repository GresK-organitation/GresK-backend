package com.gresk.modules.account.infrastructure.web.queries;

import com.gresk.modules.account.infrastructure.persistence.jpa.AccountJpaRepository;
import com.gresk.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthQueryController {

    private final AccountJpaRepository accountJpaRepository;

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = accountJpaRepository.existsByEmail(Email.of(email).value());
        return ResponseEntity.ok(Map.of("available", !exists));
    }
}