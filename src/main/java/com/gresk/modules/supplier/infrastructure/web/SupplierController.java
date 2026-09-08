package com.gresk.modules.supplier.infrastructure.web;

import com.gresk.modules.supplier.application.command.AddSupplierCatalogItemCommand;
import com.gresk.modules.supplier.application.command.RegisterSupplierCommand;
import com.gresk.modules.supplier.application.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final RegisterSupplierUseCase       registerUseCase;
    private final GetSupplierUseCase            getUseCase;
    private final SearchSuppliersUseCase        searchUseCase;
    private final AddSupplierCatalogItemUseCase addCatalogItemUseCase;
    private final DeactivateSupplierUseCase     deactivateUseCase;
    private final SupplierResponseMapper        mapper;

    // ── POST /api/v1/suppliers ────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<SupplierResponse> register(
            @RequestBody @Valid RegisterSupplierRequest request,
            @AuthenticationPrincipal String promoterId) {

        var supplier = registerUseCase.execute(new RegisterSupplierCommand(promoterId, request.name(),
                request.specialties(), request.contactName(), request.contactEmail(),
                request.contactPhone(), request.serviceCity()));
        return ResponseEntity
                .created(URI.create("/api/v1/suppliers/" + supplier.getId()))
                .body(mapper.toResponse(supplier));
    }

    // ── GET /api/v1/suppliers/{supplierId} ──────────────────────────────────
    @GetMapping("/{supplierId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<SupplierResponse> getById(@PathVariable String supplierId) {
        return ResponseEntity.ok(mapper.toResponse(getUseCase.execute(supplierId)));
    }

    // ── GET /api/v1/suppliers?category=&city= ───────────────────────────────
    @GetMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<SupplierResponse>> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @AuthenticationPrincipal String promoterId) {

        var suppliers = searchUseCase.execute(promoterId, category, city);
        return ResponseEntity.ok(suppliers.stream().map(mapper::toResponse).toList());
    }

    // ── POST /api/v1/suppliers/{supplierId}/catalog-items ────────────────────
    @PostMapping("/{supplierId}/catalog-items")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<CatalogItemResponse> addCatalogItem(
            @PathVariable String supplierId,
            @RequestBody @Valid AddCatalogItemRequest request,
            @AuthenticationPrincipal String promoterId) {

        var item = addCatalogItemUseCase.execute(new AddSupplierCatalogItemCommand(supplierId, promoterId,
                request.category(), request.itemName(), request.unitPriceAmount(), request.unitPriceCurrency(),
                request.pricingUnit(), request.leadTimeDays(), request.notes()));
        return ResponseEntity.status(201).body(mapper.toResponse(item));
    }

    // ── PATCH /api/v1/suppliers/{supplierId}/deactivate ──────────────────────
    @PatchMapping("/{supplierId}/deactivate")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<SupplierResponse> deactivate(
            @PathVariable String supplierId,
            @AuthenticationPrincipal String promoterId) {

        return ResponseEntity.ok(mapper.toResponse(deactivateUseCase.execute(supplierId, promoterId)));
    }
}
