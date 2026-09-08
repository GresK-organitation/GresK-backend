package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.domain.model.DocumentExpiryAlert;

public interface MarkDocumentExpiryAlertReadPort {
    DocumentExpiryAlert execute(String alertId, String promoterId);
}
