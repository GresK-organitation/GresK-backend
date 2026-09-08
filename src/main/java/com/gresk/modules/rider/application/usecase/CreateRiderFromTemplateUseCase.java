package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.model.RiderItemCategory;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.model.valueobject.StageDimensions;
import com.gresk.modules.rider.domain.model.valueobject.StaffMember;
import com.gresk.modules.rider.domain.port.out.RiderRepositoryPort;
import com.gresk.modules.artist.domain.exception.ArtistNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateRiderFromTemplateUseCase {

    private final RiderRepositoryPort  riderRepository;
    private final ArtistRepositoryPort artistRepository;

    @Transactional
    public TechnicalRider execute(String template, String artistId, String name, String promoterId) {
        ArtistId aid    = ArtistId.of(artistId);
        PromoterId pid  = PromoterId.of(promoterId);

        artistRepository.findByIdAndPromoterId(aid, pid)
                .orElseThrow(() -> new ArtistNotFoundException(artistId));

        TechnicalRider rider = TechnicalRider.create(aid, pid, name);
        applyTemplate(rider, template);
        return riderRepository.save(rider);
    }

    private void applyTemplate(TechnicalRider rider, String template) {
        switch (template.toLowerCase()) {
            case "rock-band" -> applyRockBandTemplate(rider);
            case "dj-set"    -> applyDjSetTemplate(rider);
            case "acoustic-duo" -> applyAcousticDuoTemplate(rider);
            default -> { /* no preset — blank rider */ }
        }
    }

    private void applyRockBandTemplate(TechnicalRider rider) {
        rider.withSoundCheck(45, "Prueba de sonido mínima 45 min sin público antes del show.");
        rider.addLineItem(RiderItemCategory.SOUND_PA, "Mesa FOH profesional", 1, true,
                Map.of("channels", "32", "monitorMixes", "6"),
                "Line Array o sistema PA adecuado a la sala. Compresor/gate por canal.");
        rider.addLineItem(RiderItemCategory.BACKLINE, "Batería completa (bombo, caja, 2 toms, 2 platillos crash, ride, hi-hat)", 1, true, null, null);
        rider.addLineItem(RiderItemCategory.BACKLINE, "Amplificador de guitarra eléctrica 50W+", 1, true, Map.of("brand", "Fender/Marshall/Mesa Boogie"), null);
        rider.addLineItem(RiderItemCategory.BACKLINE, "Amplificador de guitarra eléctrica 50W+ (segundo guitarrista)", 1, false, null, null);
        rider.addLineItem(RiderItemCategory.BACKLINE, "Amplificador de bajo 200W+ con cabina 4x10", 1, true, Map.of("brand", "Ampeg/Hartke/SWR"), null);
        rider.addLineItem(RiderItemCategory.MICROPHONE, "Micrófono vocal principal inalámbrico", 1, true, Map.of("brand", "Shure/Sennheiser", "model", "SM58"), null);
        rider.addLineItem(RiderItemCategory.MICROPHONE, "Micrófono vocal coros", 2, true, Map.of("brand", "Shure", "model", "SM58"), null);
        rider.withStageDimensions(new StageDimensions(
                new BigDecimal("6"), new BigDecimal("4"), new BigDecimal("2"),
                6, true));
        rider.withStaff(List.of(
                new StaffMember("Voz", "—"),
                new StaffMember("Guitarra", "—"),
                new StaffMember("Bajo", "—"),
                new StaffMember("Batería", "—")
        ));
    }

    private void applyDjSetTemplate(TechnicalRider rider) {
        rider.withSoundCheck(20, "20 min de prueba de líneas y monitoraje antes del show.");
        rider.addLineItem(RiderItemCategory.SOUND_PA, "Sistema PA adecuado a la sala + subwoofers", 1, true,
                Map.of("channels", "4", "monitorMixes", "1"), null);
        rider.addLineItem(RiderItemCategory.BACKLINE, "Mesa de mezclas DJ (Pioneer DJM-900 o similar)", 1, true, Map.of("brand", "Pioneer", "model", "DJM-900NXS2"), null);
        rider.addLineItem(RiderItemCategory.BACKLINE, "CDJ (Pioneer CDJ-2000NXS2 o similar)", 2, true, Map.of("brand", "Pioneer", "model", "CDJ-2000NXS2"), null);
        rider.addLineItem(RiderItemCategory.BACKLINE, "Monitor de escenario DJ (wedge)", 1, true, null, null);
        rider.withStageDimensions(new StageDimensions(
                new BigDecimal("2"), new BigDecimal("1.5"), new BigDecimal("2"),
                4, false));
    }

    private void applyAcousticDuoTemplate(TechnicalRider rider) {
        rider.withSoundCheck(30, "30 min de prueba de sonido acústico.");
        rider.addLineItem(RiderItemCategory.SOUND_PA, "Sistema PA pequeño adecuado al venue", 1, true,
                Map.of("channels", "8", "monitorMixes", "2"), null);
        rider.addLineItem(RiderItemCategory.MICROPHONE, "Micrófono condensador para instrumentos acústicos", 2, true, Map.of("brand", "AKG/Neumann", "model", "C414"), null);
        rider.addLineItem(RiderItemCategory.MICROPHONE, "Micrófono vocal", 2, true, Map.of("brand", "Shure", "model", "SM58"), null);
        rider.addLineItem(RiderItemCategory.SOUND_PA, "DI Box", 2, false, null, null);
        rider.withStageDimensions(new StageDimensions(
                new BigDecimal("3"), new BigDecimal("2"), new BigDecimal("2"),
                2, false));
    }
}
