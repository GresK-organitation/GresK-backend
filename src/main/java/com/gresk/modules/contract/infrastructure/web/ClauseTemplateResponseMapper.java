package com.gresk.modules.contract.infrastructure.web;

import com.gresk.modules.contract.domain.model.ClauseTemplate;
import com.gresk.modules.contract.infrastructure.web.dto.ClauseTemplateResponse;
import org.springframework.stereotype.Component;

@Component
public class ClauseTemplateResponseMapper {

    public ClauseTemplateResponse toResponse(ClauseTemplate t) {
        return new ClauseTemplateResponse(
                t.getId().toString(), t.getCode(), t.getCategory(), t.getTitle(), t.getContentTemplate(),
                t.getApplicableTypes(), t.getJurisdictionScope(), t.isSystemDefault(), t.getVersion(), t.isActive());
    }
}
