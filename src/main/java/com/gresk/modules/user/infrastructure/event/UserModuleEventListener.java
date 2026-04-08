package com.gresk.modules.user.infrastructure.event;

import com.gresk.modules.user.application.command.RegisterUserCommand;
import com.gresk.modules.user.domain.port.in.RegisterUserUseCase;
import com.gresk.shared.domain.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserModuleEventListener {

    private final RegisterUserUseCase registerUserUseCase;

    @EventListener
    public void on(UserRegisteredEvent event) {
        registerUserUseCase.execute(RegisterUserCommand.builder()
                .userId(event.userId())
                .email(event.email())
                .name(event.name())
                .description(event.description())
                .city(event.city())
                .musicGenres(event.musicGenres())
                .build()
        );
    }
}
