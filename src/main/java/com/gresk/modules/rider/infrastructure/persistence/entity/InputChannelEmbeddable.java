package com.gresk.modules.rider.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputChannelEmbeddable {

    @Column(name = "channel_number", nullable = false)
    private Integer channelNumber;

    @Column(name = "instrument", length = 150, nullable = false)
    private String instrument;

    @Column(name = "microphone", length = 150)
    private String microphone;

    @Column(name = "inserts", length = 150)
    private String inserts;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
