package com.gresk.modules.identity.domain.model;

import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.Role;
import com.gresk.shared.domain.valueobject.Email;

import java.time.Instant;
import java.util.Set;

public final class Account {

    private final AccountId id;
    private final Email email;
    private final String passwordHash;
    private final Set<Role> roles;
    private AccountStatus status;
    private final Instant createdAt;

    private Account(AccountId id, Email email, String passwordHash,
                    Set<Role> roles, AccountStatus status, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Account create(Email email, String passwordHash, Set<Role> roles, AccountStatus status) {
        return new Account(
                AccountId.generate(),
                email,
                passwordHash,
                roles,
                status,
                Instant.now()
        );
    }

    public static Account reconstitute(AccountId id, Email email, String passwordHash,
                                       Set<Role> roles, AccountStatus status, Instant createdAt) {
        return new Account(id, email, passwordHash, roles, status, createdAt);
    }

    public AccountId getId() { return id; }
    public Email getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Set<Role> getRoles() { return roles; }
    public AccountStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
