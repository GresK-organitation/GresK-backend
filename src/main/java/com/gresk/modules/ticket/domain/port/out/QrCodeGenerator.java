package com.gresk.modules.ticket.domain.port.out;

import com.gresk.modules.ticket.domain.model.QrCode;
import com.gresk.modules.ticket.domain.model.TicketId;

public interface QrCodeGenerator {

    QrCode generate(TicketId ticketId);

    byte[] renderToImage(QrCode qrCode);
}
