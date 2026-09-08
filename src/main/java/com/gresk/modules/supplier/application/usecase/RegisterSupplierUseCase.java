package com.gresk.modules.supplier.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.supplier.application.command.RegisterSupplierCommand;
import com.gresk.modules.supplier.domain.model.Supplier;
import com.gresk.modules.supplier.domain.model.valueobject.SupplierCategory;
import com.gresk.modules.supplier.domain.model.valueobject.SupplierContact;
import com.gresk.modules.supplier.domain.port.out.SupplierRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterSupplierUseCase {

    private final SupplierRepositoryPort supplierRepository;

    @Transactional
    public Supplier execute(RegisterSupplierCommand command) {
        Set<SupplierCategory> specialties = command.specialties() == null ? Set.of() :
                command.specialties().stream().map(SupplierCategory::valueOf).collect(Collectors.toSet());

        SupplierContact contact = command.contactName() == null ? null :
                new SupplierContact(command.contactName(), command.contactEmail(), command.contactPhone());

        Supplier supplier = Supplier.create(PromoterId.of(command.promoterId()), command.name(),
                specialties, contact, command.serviceCity());

        return supplierRepository.save(supplier);
    }
}
