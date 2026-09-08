package com.gresk.modules.finance.domain.port.out;

import com.gresk.modules.finance.domain.model.Invoice;

public interface InvoicePdfRendererPort {
    byte[] render(Invoice invoice);
}
