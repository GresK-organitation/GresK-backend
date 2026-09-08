package com.gresk.modules.contract.domain.port.out;

import com.gresk.modules.contract.domain.model.Contract;

import java.util.Map;

/**
 * Resuelve variables de plantilla ({{event.venue}}, {{artist.name}}, ...) consultando
 * los puertos públicos de otros módulos (event, artist), sin que el dominio de contract
 * dependa de sus internals.
 */
public interface ContractVariableResolverPort {
    Map<String, Object> resolveVariables(Contract contract);
}
