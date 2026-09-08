package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.rider.domain.exception.RiderNotFoundException;
import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.port.out.HospitalityRiderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetHospitalityRiderUseCase {

    private final HospitalityRiderRepositoryPort riderRepository;

    @Transactional(readOnly = true)
    public HospitalityRider execute(String riderId) {
        return riderRepository.findById(RiderId.of(riderId))
                .orElseThrow(() -> new RiderNotFoundException(riderId));
    }
}
