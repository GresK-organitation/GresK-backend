package com.gresk.modules.contract.domain.port.out;

import java.util.Map;

public interface TemplateRenderingPort {
    byte[] renderToPdf(String bodyMarkdown, Map<String, Object> variables);
}
