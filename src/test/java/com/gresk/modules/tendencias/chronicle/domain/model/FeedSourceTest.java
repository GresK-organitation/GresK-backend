package com.gresk.modules.tendencias.chronicle.domain.model;

import com.gresk.modules.tendencias.chronicle.domain.exception.InvalidFeedSourceTransitionException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedSourceTest {

    @Test
    void registerCreaLaFuenteEnPendingApproval() {
        FeedSource source = FeedSource.register("Mondo Sonoro", "https://mondosonoro.com/feed", "https://mondosonoro.com");
        assertThat(source.getStatus()).isEqualTo(FeedSourceStatus.PENDING_APPROVAL);
    }

    @Test
    void approveHabilitaElPolling() {
        FeedSource source = FeedSource.register("Enderrock", "https://enderrock.cat/feed", null);
        UUID adminId = UUID.randomUUID();
        source.approve(adminId);
        assertThat(source.getStatus()).isEqualTo(FeedSourceStatus.APPROVED);
        assertThat(source.getReviewedBy()).isEqualTo(adminId);
    }

    @Test
    void rejectDosVecesLanzaExcepcion() {
        FeedSource source = FeedSource.register("Substack X", "https://x.substack.com/feed", null);
        UUID adminId = UUID.randomUUID();
        source.reject(adminId);
        assertThatThrownBy(() -> source.reject(adminId))
                .isInstanceOf(InvalidFeedSourceTransitionException.class);
    }
}
