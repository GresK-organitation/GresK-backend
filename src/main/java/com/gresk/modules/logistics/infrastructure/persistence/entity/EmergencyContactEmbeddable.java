package com.gresk.modules.logistics.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactEmbeddable {

    @Column(name = "contact_name", nullable = false)
    private String name;

    @Column(name = "contact_role")
    private String role;

    @Column(name = "contact_phone", nullable = false)
    private String phone;

    @Column(name = "contact_notes", columnDefinition = "TEXT")
    private String notes;
}
