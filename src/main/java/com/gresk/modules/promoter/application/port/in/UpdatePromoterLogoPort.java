package com.gresk.modules.promoter.application.port.in;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UpdatePromoterLogoPort {
    void execute(UUID accountId, MultipartFile file);
}
