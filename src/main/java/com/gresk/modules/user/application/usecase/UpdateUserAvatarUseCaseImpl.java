package com.gresk.modules.user.application.usecase;

import com.gresk.modules.user.domain.exception.UserNotFoundException;
import com.gresk.modules.user.domain.model.User;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.modules.user.domain.port.in.UpdateUserAvatarUseCase;
import com.gresk.modules.user.domain.port.out.UserRepositoryPort;
import com.gresk.shared.domain.port.out.ImageStoragePort;
import com.gresk.shared.domain.valueobject.AssetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateUserAvatarUseCaseImpl implements UpdateUserAvatarUseCase {

    private final UserRepositoryPort userRepository;
    private final ImageStoragePort imageStorage;

    @Transactional
    @Override
    public void execute(UUID userId, MultipartFile file) {
        UserId id = UserId.of(userId);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        AssetId assetId = imageStorage.upload(file, "users/avatars");
        user.updateAvatar(assetId);

        userRepository.save(user);
    }
}
