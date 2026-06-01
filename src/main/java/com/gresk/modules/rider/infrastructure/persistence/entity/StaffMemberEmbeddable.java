package com.gresk.modules.rider.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffMemberEmbeddable {

    @Column(name = "role", length = 100, nullable = false)
    private String role;

    @Column(name = "name", length = 100, nullable = false)
    private String name;
}
