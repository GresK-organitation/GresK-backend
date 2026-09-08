package com.gresk.modules.show.application.service;

import com.gresk.modules.show.application.command.UpdateShowDetailsCommand;
import com.gresk.modules.show.application.port.in.UpdateShowDetailsUseCase;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateShowDetailsService implements UpdateShowDetailsUseCase {

    private final ShowRepositoryPort showRepository;

    @Override
    public Show execute(UpdateShowDetailsCommand command) {
        Show show = ShowLookup.findOwned(showRepository, command.showId(), command.promoterId());
        show.updateDetails(command.name(), command.scheduledDate());
        return showRepository.save(show);
    }
}
