package com.gresk.modules.booking;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Reglas de arquitectura hexagonal del módulo booking:
 * el dominio no conoce las capas externas y la aplicación no conoce
 * la infraestructura (solo los puertos del dominio).
 */
@AnalyzeClasses(packages = "com.gresk.modules.booking",
                importOptions = ImportOption.DoNotIncludeTests.class)
class BookingModuleArchitectureTest {

    @ArchTest
    static final ArchRule elDominioNoDependeDeCapasExternas =
            noClasses().that().resideInAPackage("..modules.booking.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..modules.booking.application..",
                                        "..modules.booking.infrastructure..");

    @ArchTest
    static final ArchRule laAplicacionNoDependeDeInfraestructura =
            noClasses().that().resideInAPackage("..modules.booking.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..modules.booking.infrastructure..");

    @ArchTest
    static final ArchRule elDominioNoUsaJpa =
            noClasses().that().resideInAPackage("..modules.booking.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.persistence..", "org.hibernate..");
}
