package com.gresk.modules.rider.domain.model;

import com.gresk.modules.rider.domain.exception.SubstitutionNotProposedException;
import com.gresk.modules.rider.domain.model.valueobject.EquipmentEquivalence;
import com.gresk.modules.rider.domain.model.valueobject.FulfillmentSource;
import com.gresk.modules.rider.domain.model.valueobject.SubstitutionStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class RiderLineItem {

    private final UUID id;
    private final RiderItemCategory category;
    private final String description;
    private final int quantity;
    private final boolean required;
    private final Map<String, String> attributes;
    private FulfillmentSource fulfillmentSource;
    private EquipmentEquivalence equivalence;
    private final String notes;

    private RiderLineItem(UUID id, RiderItemCategory category, String description, int quantity, boolean required,
                           Map<String, String> attributes, FulfillmentSource fulfillmentSource,
                           EquipmentEquivalence equivalence, String notes) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.required = required;
        this.attributes = attributes != null ? new HashMap<>(attributes) : new HashMap<>();
        this.fulfillmentSource = fulfillmentSource;
        this.equivalence = equivalence;
        this.notes = notes;
    }

    public static RiderLineItem create(RiderItemCategory category, String description, int quantity,
                                        boolean required, Map<String, String> attributes, String notes) {
        if (category == null)
            throw new IllegalArgumentException("RiderLineItem category cannot be null");
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("RiderLineItem description cannot be blank");
        if (quantity <= 0)
            throw new IllegalArgumentException("RiderLineItem quantity must be positive");
        return new RiderLineItem(UUID.randomUUID(), category, description, quantity, required,
                attributes, FulfillmentSource.UNRESOLVED, null, notes);
    }

    public static RiderLineItem reconstitute(UUID id, RiderItemCategory category, String description, int quantity,
                                              boolean required, Map<String, String> attributes,
                                              FulfillmentSource fulfillmentSource, EquipmentEquivalence equivalence,
                                              String notes) {
        return new RiderLineItem(id, category, description, quantity, required, attributes,
                fulfillmentSource, equivalence, notes);
    }

    public void setFulfillment(FulfillmentSource source) {
        if (source == null) throw new IllegalArgumentException("FulfillmentSource cannot be null");
        this.fulfillmentSource = source;
    }

    public void proposeEquivalence(String proposedAlternative, EquipmentEquivalence.ProposedBy proposedBy, String notes) {
        this.equivalence = EquipmentEquivalence.propose(description, proposedAlternative, proposedBy, notes);
    }

    public void decideEquivalence(SubstitutionStatus decision) {
        if (equivalence == null) throw new SubstitutionNotProposedException(id);
        this.equivalence = equivalence.decide(decision);
    }

    public UUID getId() { return id; }
    public RiderItemCategory getCategory() { return category; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public boolean isRequired() { return required; }
    public Map<String, String> getAttributes() { return Map.copyOf(attributes); }
    public FulfillmentSource getFulfillmentSource() { return fulfillmentSource; }
    public Optional<EquipmentEquivalence> getEquivalence() { return Optional.ofNullable(equivalence); }
    public String getNotes() { return notes; }
}
