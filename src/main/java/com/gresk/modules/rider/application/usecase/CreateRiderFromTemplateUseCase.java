package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.model.*;
import com.gresk.modules.rider.domain.model.valueobject.*;
import com.gresk.modules.rider.domain.port.out.RiderRepositoryPort;
import com.gresk.modules.artist.domain.exception.ArtistNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
        rider.withSoundSystem(new SoundSystemRequirements(
                "Cualquier marca profesional", 32, 6,
                "Line Array o sistema PA adecuado a la sala", "Compresor/gate por canal"));
        rider.withBacklineItems(List.of(
                new BacklineItem(BacklineCategory.DRUMS, "Batería completa (bombo, caja, 2 toms, 2 platillos crash, ride, hi-hat)", null, null, true),
                new BacklineItem(BacklineCategory.GUITARS, "Amplificador de guitarra eléctrica 50W+", "Fender/Marshall/Mesa Boogie", null, true),
                new BacklineItem(BacklineCategory.GUITARS, "Amplificador de guitarra eléctrica 50W+ (segundo guitarrista)", null, null, false),
                new BacklineItem(BacklineCategory.BASS, "Amplificador de bajo 200W+ con cabina 4x10", "Ampeg/Hartke/SWR", null, true),
                new BacklineItem(BacklineCategory.VOCALS, "Micrófono vocal principal inalámbrico", "Shure/Sennheiser", "SM58", true),
                new BacklineItem(BacklineCategory.VOCALS, "Micrófono vocal coros x2", "Shure", "SM58", true)
        ));
        rider.withStageDimensions(new StageDimensions(
                new BigDecimal("6"), new BigDecimal("4"), new BigDecimal("2"),
                6, true));
        rider.withHospitality(new HospitalityRequirements(8, "2 comidas + bebidas por integrante", 6, 4));
        rider.withTransport(new TransportRequirements("Furgoneta 9 plazas", 9, "Transporte ida y vuelta al venue"));
        rider.withStaff(List.of(
                new StaffMember("Voz", "—"),
                new StaffMember("Guitarra", "—"),
                new StaffMember("Bajo", "—"),
                new StaffMember("Batería", "—")
        ));
    }

    private void applyDjSetTemplate(TechnicalRider rider) {
        rider.withSoundCheck(20, "20 min de prueba de líneas y monitoraje antes del show.");
        rider.withSoundSystem(new SoundSystemRequirements(
                "Cualquier marca profesional", 4, 1,
                "Sistema PA adecuado a la sala + subwoofers", null));
        rider.withBacklineItems(List.of(
                new BacklineItem(BacklineCategory.SOUND, "Mesa de mezclas DJ (Pioneer DJM-900 o similar)", "Pioneer", "DJM-900NXS2", true),
                new BacklineItem(BacklineCategory.SOUND, "2× CDJ (Pioneer CDJ-2000NXS2 o similar)", "Pioneer", "CDJ-2000NXS2", true),
                new BacklineItem(BacklineCategory.SOUND, "Monitor de escenario DJ (wedge)", null, null, true)
        ));
        rider.withStageDimensions(new StageDimensions(
                new BigDecimal("2"), new BigDecimal("1.5"), new BigDecimal("2"),
                4, false));
        rider.withHospitality(new HospitalityRequirements(2, "Bebidas + snacks", 2, 2));
        rider.withTransport(new TransportRequirements("Taxi / VTC", 2, null));
    }

    private void applyAcousticDuoTemplate(TechnicalRider rider) {
        rider.withSoundCheck(30, "30 min de prueba de sonido acústico.");
        rider.withSoundSystem(new SoundSystemRequirements(
                "Cualquier marca", 8, 2,
                "Sistema PA pequeño adecuado al venue", null));
        rider.withBacklineItems(List.of(
                new BacklineItem(BacklineCategory.SOUND, "2× Micrófono condensador para instrumentos acústicos", "AKG/Neumann", "C414", true),
                new BacklineItem(BacklineCategory.VOCALS, "2× Micrófono vocal", "Shure", "SM58", true),
                new BacklineItem(BacklineCategory.SOUND, "2× DI Box", null, null, false)
        ));
        rider.withStageDimensions(new StageDimensions(
                new BigDecimal("3"), new BigDecimal("2"), new BigDecimal("2"),
                2, false));
        rider.withHospitality(new HospitalityRequirements(3, "Bebidas + bocadillos", 2, 2));
    }
}
