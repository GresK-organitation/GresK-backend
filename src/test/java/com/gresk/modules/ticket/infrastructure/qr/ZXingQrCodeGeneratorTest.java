package com.gresk.modules.ticket.infrastructure.qr;

import com.gresk.modules.ticket.domain.model.QrCode;
import com.gresk.modules.ticket.domain.model.TicketId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ZXingQrCodeGeneratorTest {

    private ZXingQrCodeGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ZXingQrCodeGenerator();
    }

    @Test
    void generate_returnsNonNullNonEmptyQrCode() {
        TicketId ticketId = TicketId.generate();

        QrCode result = generator.generate(ticketId);

        assertThat(result).isNotNull();
        assertThat(result.value()).isNotBlank();
    }

    @Test
    void renderToImage_returnsValidPng() {
        QrCode qrCode = QrCode.of("test-token");

        byte[] result = generator.renderToImage(qrCode);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(4);
        // PNG magic bytes: 0x89 0x50 0x4E 0x47 (0x89 P N G)
        assertThat(result[0]).isEqualTo((byte) 0x89);
        assertThat(result[1]).isEqualTo((byte) 'P');
        assertThat(result[2]).isEqualTo((byte) 'N');
        assertThat(result[3]).isEqualTo((byte) 'G');
    }

    @Test
    void generate_twoDifferentTicketIdProduceTwoDifferentQrCodes() {
        TicketId ticketId1 = TicketId.generate();
        TicketId ticketId2 = TicketId.generate();

        QrCode qrCode1 = generator.generate(ticketId1);
        QrCode qrCode2 = generator.generate(ticketId2);

        assertThat(qrCode1).isNotEqualTo(qrCode2);
    }
}
