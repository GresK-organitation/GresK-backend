package com.gresk.modules.contract.domain.model;

import com.gresk.modules.contract.domain.model.valueobject.TemplateVariable;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;
import java.util.List;

/** Plantilla de documento completo (Markdown/HTML) que Contract renderiza sustituyendo variables. */
public final class ContractTemplate {

    private final ContractTemplateId   id;
    private final PromoterId           promoterId;   // null = plantilla de sistema
    private final ContractType         type;
    private String                     name;
    private String                     bodyMarkdown;
    private List<TemplateVariable>    variables;
    private List<ClauseTemplateId>    defaultClauseTemplateIds;
    private int                        version;
    private boolean                    active;
    private final Instant              createdAt;

    private ContractTemplate(ContractTemplateId id, PromoterId promoterId, ContractType type, String name,
                              String bodyMarkdown, List<TemplateVariable> variables,
                              List<ClauseTemplateId> defaultClauseTemplateIds, int version, boolean active,
                              Instant createdAt) {
        this.id                       = id;
        this.promoterId               = promoterId;
        this.type                     = type;
        this.name                     = name;
        this.bodyMarkdown             = bodyMarkdown;
        this.variables                = List.copyOf(variables);
        this.defaultClauseTemplateIds = List.copyOf(defaultClauseTemplateIds);
        this.version                  = version;
        this.active                   = active;
        this.createdAt                = createdAt;
    }

    public static ContractTemplate create(PromoterId promoterId, ContractType type, String name,
                                           String bodyMarkdown, List<TemplateVariable> variables,
                                           List<ClauseTemplateId> defaultClauseTemplateIds) {
        return new ContractTemplate(ContractTemplateId.generate(), promoterId, type, name, bodyMarkdown,
                variables, defaultClauseTemplateIds, 1, true, Instant.now());
    }

    public static ContractTemplate reconstitute(ContractTemplateId id, PromoterId promoterId, ContractType type,
                                                 String name, String bodyMarkdown, List<TemplateVariable> variables,
                                                 List<ClauseTemplateId> defaultClauseTemplateIds, int version,
                                                 boolean active, Instant createdAt) {
        return new ContractTemplate(id, promoterId, type, name, bodyMarkdown, variables,
                defaultClauseTemplateIds, version, active, createdAt);
    }

    public void update(String name, String bodyMarkdown, List<TemplateVariable> variables,
                        List<ClauseTemplateId> defaultClauseTemplateIds) {
        this.name                     = name;
        this.bodyMarkdown             = bodyMarkdown;
        this.variables                = List.copyOf(variables);
        this.defaultClauseTemplateIds = List.copyOf(defaultClauseTemplateIds);
        this.version                  = version + 1;
    }

    public void deactivate() { this.active = false; }

    public boolean isOwnedBy(PromoterId candidate) {
        return promoterId != null && promoterId.equals(candidate);
    }

    public ContractTemplateId          getId()                       { return id; }
    public PromoterId                  getPromoterId()               { return promoterId; }
    public ContractType                getType()                     { return type; }
    public String                      getName()                     { return name; }
    public String                      getBodyMarkdown()             { return bodyMarkdown; }
    public List<TemplateVariable>      getVariables()                { return variables; }
    public List<ClauseTemplateId>      getDefaultClauseTemplateIds() { return defaultClauseTemplateIds; }
    public int                         getVersion()                  { return version; }
    public boolean                     isActive()                    { return active; }
    public Instant                      getCreatedAt()                { return createdAt; }
}
