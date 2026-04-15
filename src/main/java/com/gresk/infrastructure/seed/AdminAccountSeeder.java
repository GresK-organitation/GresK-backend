package com.gresk.infrastructure.seed;

import com.gresk.modules.account.application.port.out.PasswordHasherPort;
import com.gresk.modules.account.infrastructure.persistence.AccountEntity;
import com.gresk.modules.account.infrastructure.persistence.AccountJpaRepository;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

/**
 * Crea la cuenta de admin por defecto al arrancar la aplicación,
 * si no existe ninguna cuenta con rol ADMIN.
 *
 * Credenciales por defecto:
 *   email:    admin@gresk.com
 *   password: admin123  (BCrypt — hash generado en runtime)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountSeeder {

    private static final String ADMIN_EMAIL    = "admin@gresk.com";
    private static final String ADMIN_PASSWORD = "admin123";

    private final AccountJpaRepository accountJpaRepository;
    private final PasswordHasherPort   passwordHasher;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seed() {
        if (accountJpaRepository.existsByRolesContaining(Role.ADMIN)) {
            log.info("[AdminSeeder] Admin account already exists — skipping.");
            return;
        }

        String hash = passwordHasher.hash(ADMIN_PASSWORD);

        AccountEntity admin = AccountEntity.builder()
                .id(UUID.randomUUID())
                .email(ADMIN_EMAIL)
                .passwordHash(hash)
                .status(AccountStatus.ACTIVE)
                .roles(Set.of(Role.ADMIN))
                .build();

        accountJpaRepository.save(admin);
        log.info("[AdminSeeder] Default admin account created → {}", ADMIN_EMAIL);
    }
}
