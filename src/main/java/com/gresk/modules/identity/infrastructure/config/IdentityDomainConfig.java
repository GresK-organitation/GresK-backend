package com.gresk.modules.identity.infrastructure.config;

import com.gresk.modules.identity.domain.port.out.AccountRepositoryPort;
import com.gresk.modules.identity.domain.service.UserEmailUniquenessChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityDomainConfig {

    @Bean
    public UserEmailUniquenessChecker userEmailUniquenessChecker(AccountRepositoryPort accountRepository) {
        return new UserEmailUniquenessChecker(accountRepository);
    }
}