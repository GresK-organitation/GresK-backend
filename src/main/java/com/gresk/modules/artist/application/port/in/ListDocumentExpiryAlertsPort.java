package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.domain.model.DocumentExpiryAlert;

import java.util.List;

public interface ListDocumentExpiryAlertsPort {
    List<DocumentExpiryAlert> execute(String promoterId);
}
