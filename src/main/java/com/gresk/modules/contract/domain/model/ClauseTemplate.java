package com.gresk.modules.contract.domain.model;

import com.gresk.modules.contract.domain.model.valueobject.ClauseCategory;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;
import java.util.List;

/**
 * Entrada del catálogo reutilizable de cláusulas. ContractClause (aplicada a un
 * contrato) es un snapshot congelado de esta plantilla: editar un ClauseTemplate NUNCA
 * altera contratos ya emitidos, solo futuras aplicaciones.
 */
public final class ClauseTemplate {

    private final ClauseTemplateId     id;
    private final PromoterId           promoterId;   // null = plantilla de sistema
    private final String               code;
    private final ClauseCategory       category;
    private String                     title;
    private String                     contentTemplate;
    private List<ContractType>        applicableTypes;
    private String                     jurisdictionScope;
    private final boolean              systemDefault;
    private int                        version;
    private boolean                    active;

    private ClauseTemplate(ClauseTemplateId id, PromoterId promoterId, String code, ClauseCategory category,
                            String title, String contentTemplate, List<ContractType> applicableTypes,
                            String jurisdictionScope, boolean systemDefault, int version, boolean active) {
        this.id                = id;
        this.promoterId         = promoterId;
        this.code               = code;
        this.category           = category;
        this.title              = title;
        this.contentTemplate    = contentTemplate;
        this.applicableTypes    = List.copyOf(applicableTypes);
        this.jurisdictionScope  = jurisdictionScope;
        this.systemDefault      = systemDefault;
        this.version            = version;
        this.active             = active;
    }

    public static ClauseTemplate createCustom(PromoterId promoterId, String code, ClauseCategory category,
                                               String title, String contentTemplate,
                                               List<ContractType> applicableTypes, String jurisdictionScope) {
        return new ClauseTemplate(ClauseTemplateId.generate(), promoterId, code, category, title,
                contentTemplate, applicableTypes, jurisdictionScope, false, 1, true);
    }

    public static ClauseTemplate reconstitute(ClauseTemplateId id, PromoterId promoterId, String code,
                                               ClauseCategory category, String title, String contentTemplate,
                                               List<ContractType> applicableTypes, String jurisdictionScope,
                                               boolean systemDefault, int version, boolean active) {
        return new ClauseTemplate(id, promoterId, code, category, title, contentTemplate,
                applicableTypes, jurisdictionScope, systemDefault, version, active);
    }

    public void reviseContent(String newTitle, String newContent) {
        this.title           = newTitle;
        this.contentTemplate = newContent;
        this.version          = version + 1;
    }

    public void deactivate() { this.active = false; }
    public void activate()   { this.active = true; }

    public boolean isOwnedBy(PromoterId candidate) {
        return !systemDefault && promoterId != null && promoterId.equals(candidate);
    }

    public ClauseTemplateId      getId()                { return id; }
    public PromoterId            getPromoterId()        { return promoterId; }
    public String                getCode()              { return code; }
    public ClauseCategory        getCategory()          { return category; }
    public String                getTitle()             { return title; }
    public String                getContentTemplate()   { return contentTemplate; }
    public List<ContractType>    getApplicableTypes()   { return applicableTypes; }
    public String                getJurisdictionScope() { return jurisdictionScope; }
    public boolean               isSystemDefault()      { return systemDefault; }
    public int                   getVersion()           { return version; }
    public boolean               isActive()             { return active; }
}
