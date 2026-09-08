package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.exception.ContractNotFoundException;
import com.gresk.modules.contract.domain.exception.ContractNotOwnedException;
import com.gresk.modules.contract.domain.exception.ContractTemplateNotFoundException;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.ContractTemplate;
import com.gresk.modules.contract.domain.model.ContractTemplateId;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.contract.domain.port.out.ContractTemplateRepositoryPort;
import com.gresk.modules.contract.domain.port.out.ContractVariableResolverPort;
import com.gresk.modules.contract.domain.port.out.TemplateRenderingPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Pipeline nuevo y separado del ya existente GenerateContractPdfUseCase (OpenPDF):
 * renderiza el ContractTemplate (Markdown) resolviendo variables de Event/Artist.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RenderContractFromTemplateUseCase {

    private final ContractRepositoryPort         contractRepository;
    private final ContractTemplateRepositoryPort contractTemplateRepository;
    private final ContractVariableResolverPort   variableResolver;
    private final TemplateRenderingPort          templateRenderer;

    public byte[] execute(String contractId, String templateId, String promoterId) {
        Contract contract = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new ContractNotFoundException(contractId));
        if (!contract.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new ContractNotOwnedException();
        }
        ContractTemplate template = contractTemplateRepository.findById(ContractTemplateId.of(templateId))
                .orElseThrow(() -> new ContractTemplateNotFoundException(templateId));

        Map<String, Object> variables = variableResolver.resolveVariables(contract);
        return templateRenderer.renderToPdf(template.getBodyMarkdown(), variables);
    }
}
