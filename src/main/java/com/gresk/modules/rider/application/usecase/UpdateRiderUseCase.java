package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.application.command.UpdateRiderCommand;
import com.gresk.modules.rider.domain.exception.RiderNotFoundException;
import com.gresk.modules.rider.domain.exception.RiderNotOwnedException;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.model.StageElementType;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.model.valueobject.StageDimensions;
import com.gresk.modules.rider.domain.model.valueobject.StageElement;
import com.gresk.modules.rider.domain.model.valueobject.StaffMember;
import com.gresk.modules.rider.domain.port.out.RiderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateRiderUseCase {

    private final RiderRepositoryPort riderRepository;

    @Transactional
    public TechnicalRider execute(UpdateRiderCommand command) {
        TechnicalRider rider = riderRepository.findById(RiderId.of(command.riderId()))
                .orElseThrow(() -> new RiderNotFoundException(command.riderId()));

        if (!rider.getPromoterId().equals(PromoterId.of(command.promoterId()))) {
            throw new RiderNotOwnedException(command.riderId());
        }

        boolean changed = false;

        if (command.name() != null) {
            rider.withName(command.name());
            changed = true;
        }
        if (command.soundCheckDurationMinutes() != null || command.soundCheckNotes() != null) {
            rider.withSoundCheck(command.soundCheckDurationMinutes(), command.soundCheckNotes());
            changed = true;
        }
        if (command.staff() != null) {
            rider.withStaff(command.staff().stream()
                    .map(s -> new StaffMember(s.role(), s.name())).toList());
            changed = true;
        }
        if (command.stageDimensions() != null) {
            UpdateRiderCommand.StageDimensionsData sd = command.stageDimensions();
            rider.withStageDimensions(new StageDimensions(
                    sd.widthMeters(), sd.depthMeters(), sd.minHeightMeters(),
                    sd.powerOutlets(), Boolean.TRUE.equals(sd.hasDrumRiser())));
            changed = true;
        }
        if (command.stageElements() != null) {
            rider.withStageElements(command.stageElements().stream()
                    .map(e -> new StageElement(e.elementId(),
                            StageElementType.valueOf(e.type()),
                            e.xPercent(), e.yPercent(),
                            e.rotationDegrees(), e.label())).toList());
            changed = true;
        }
        if (command.additionalNotes() != null) {
            rider.withAdditionalNotes(command.additionalNotes());
            changed = true;
        }

        if (changed) rider.incrementVersion();

        return riderRepository.save(rider);
    }
}
