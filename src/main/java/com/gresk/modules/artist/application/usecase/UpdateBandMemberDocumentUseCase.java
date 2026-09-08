package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.command.UpdateBandMemberDocumentCommand;
import com.gresk.modules.artist.application.port.in.UpdateBandMemberDocumentPort;
import com.gresk.modules.artist.domain.exception.BandMemberNotFoundException;
import com.gresk.modules.artist.domain.model.BandMember;
import com.gresk.modules.artist.domain.model.valueobject.BandMemberId;
import com.gresk.modules.artist.domain.model.valueobject.IdentityDocument;
import com.gresk.modules.artist.domain.model.valueobject.IdentityDocumentType;
import com.gresk.modules.artist.domain.port.out.BandMemberRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateBandMemberDocumentUseCase implements UpdateBandMemberDocumentPort {

    private final BandMemberRepositoryPort bandMemberRepository;

    @Override
    public BandMember execute(UpdateBandMemberDocumentCommand command) {
        BandMember member = bandMemberRepository
                .findByIdAndPromoterId(BandMemberId.of(command.bandMemberId()), PromoterId.of(command.promoterId()))
                .orElseThrow(() -> new BandMemberNotFoundException(command.bandMemberId()));

        member.addOrRenewDocument(IdentityDocument.of(
                IdentityDocumentType.valueOf(command.documentType()),
                command.documentNumber(),
                command.issuingCountry(),
                command.expiryDate()
        ));

        return bandMemberRepository.save(member);
    }
}
