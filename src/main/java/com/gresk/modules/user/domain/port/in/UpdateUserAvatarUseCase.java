package com.gresk.modules.user.domain.port.in;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UpdateUserAvatarUseCase {
    void execute(UUID userId, MultipartFile file);
}
