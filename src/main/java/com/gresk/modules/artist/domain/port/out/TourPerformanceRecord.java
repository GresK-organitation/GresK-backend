package com.gresk.modules.artist.domain.port.out;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Registro de rendimiento de un evento pasado de un artista. NO es una
 * entidad persistida: es el resultado agregado de consultar los módulos
 * event/ticket/contract en el momento de la lectura.
 *
 * estimatedMargin es una aproximación de margen bruto (ingresos por venta de
 * entradas − caché pagado según contrato), NO rentabilidad real: el sistema
 * hoy no captura gastos de producción (catering, transporte, backline).
 */
public record TourPerformanceRecord(
        String eventId,
        String eventTitle,
        LocalDate eventDate,
        String city,
        String venue,
        Integer capacity,
        Integer ticketsSold,
        BigDecimal grossRevenue,
        String revenueCurrency,
        BigDecimal feePaid,
        String feeCurrency,
        BigDecimal estimatedMargin,
        String contractStatus
) {}
