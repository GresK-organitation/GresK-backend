package com.gresk.infrastructure.seed;

import com.gresk.modules.account.application.port.out.PasswordHasherPort;
import com.gresk.modules.account.infrastructure.persistence.AccountEntity;
import com.gresk.modules.account.infrastructure.persistence.AccountJpaRepository;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class AdminAccountSeeder {

    private final AccountJpaRepository accountJpaRepository;
    private final PasswordHasherPort   passwordHasher;

    @Value("${ADMIN_EMAIL:#{null}}")
    @Nullable
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:#{null}}")
    @Nullable
    private String adminPassword;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seed() {
        if (adminEmail == null || adminPassword == null) {
            log.warn("[AdminSeeder] ADMIN_EMAIL or ADMIN_PASSWORD not set — skipping admin creation.");
            return;
        }

        if (accountJpaRepository.existsByRolesContaining(Role.ADMIN)) {
            log.info("[AdminSeeder] Admin account already exists — skipping.");
            return;
        }

        AccountEntity admin = AccountEntity.builder()
                .id(UUID.randomUUID())
                .email(adminEmail)
                .passwordHash(passwordHasher.hash(adminPassword))
                .status(AccountStatus.ACTIVE)
                .roles(Set.of(Role.ADMIN))
                .build();

        accountJpaRepository.save(admin);
        log.info("[AdminSeeder] Admin account created → {}", adminEmail);
    }
}
