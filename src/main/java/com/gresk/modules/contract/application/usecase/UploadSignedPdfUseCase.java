package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.exception.ContractNotFoundException;
import com.gresk.modules.contract.domain.exception.ContractNotOwnedException;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.port.out.RawFileStoragePort;
import com.gresk.shared.domain.valueobject.AssetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class UploadSignedPdfUseCase {

    private final ContractRepositoryPort contractRepository;
    private final RawFileStoragePort     rawFileStoragePort;

    public Contract execute(String contractId, String promoterId, MultipartFile file) {
        Contract contract = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new ContractNotFoundException(contractId));
        if (!contract.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new ContractNotOwnedException();
        }

        if (contract.getSignedPdfAssetId() != null) {
            rawFileStoragePort.delete(AssetId.of(contract.getSignedPdfAssetId()));
        }

        AssetId assetId = rawFileStoragePort.upload(
                file, "contracts/signed/" + promoterId);
        contract.withSignedPdfAssetId(assetId.value());

        return contractRepository.save(contract);
    }
}
