package com.gresk.shared.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Activa el subsistema de tareas programadas de Spring.
 * Sin esta anotación, @Scheduled es ignorado silenciosamente en toda la aplicación.
 *
 * Separado de GreskBackendApplication para mantener SRP:
 * la clase principal solo arranca el contexto; la configuración
 * de scheduling es una responsabilidad de infraestructura independiente.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {}
