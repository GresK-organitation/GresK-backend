package com.gresk.shared.port;

import com.gresk.modules.promoter.domain.valueobject.Email;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;

public interface JwtTokenGenerator {
    AuthToken generate(PromoterId subject, Email email);
}
