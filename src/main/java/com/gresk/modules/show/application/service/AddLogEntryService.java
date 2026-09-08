package com.gresk.modules.show.application.service;

import com.gresk.modules.show.application.command.AddLogEntryCommand;
import com.gresk.modules.show.application.port.in.AddLogEntryUseCase;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.ShowLogEntry;
import com.gresk.modules.show.domain.port.out.ShowLogRepositoryPort;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import com.gresk.shared.domain.valueobject.AssetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddLogEntryService implements AddLogEntryUseCase {

    private final ShowRepositoryPort showRepository;
    private final ShowLogRepositoryPort showLogRepository;

    @Override
    public ShowLogEntry execute(AddLogEntryCommand command) {
        Show show = ShowLookup.findOwned(showRepository, command.showId(), command.promoterId());

        List<AssetId> attachments = command.attachmentAssetIds() == null ? List.of()
                : command.attachmentAssetIds().stream().map(AssetId::of).toList();

        ShowLogEntry entry = ShowLogEntry.record(show.getId(), command.type(), command.actor(),
                command.description(), command.relatedParty(), attachments);
        return showLogRepository.save(entry);
    }
}
