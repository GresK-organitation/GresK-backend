package com.gresk.modules.finance.domain.model;

public enum InstallmentStatus {
    PENDING,
    PAID,
    /** Nunca se persiste — se deriva en lectura comparando dueDate con la fecha actual. */
    OVERDUE,
    CANCELLED
}
