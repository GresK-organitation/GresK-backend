package com.gresk.modules.contract.domain.model.valueobject;

public record TemplateVariable(
        String  path,
        String  label,
        boolean required
) {}
