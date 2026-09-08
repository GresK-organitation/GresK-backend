package com.gresk.modules.show.application.service;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.show.application.command.CreateShowCommand;
import com.gresk.modules.show.application.port.in.CreateShowUseCase;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateShowService implements CreateShowUseCase {

    private final ShowRepositoryPort showRepository;

    @Override
    public Show execute(CreateShowCommand command) {
        Show show = Show.create(PromoterId.of(command.promoterId()), command.name(), command.tentativeDate());
        return showRepository.save(show);
    }
}
