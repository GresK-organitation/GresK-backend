package com.gresk.modules.agenda;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Reglas de arquitectura hexagonal del módulo agenda (agenda personal + sync de calendario
 * externo): el dominio no conoce las capas externas y la aplicación no conoce la
 * infraestructura (solo los puertos del dominio).
 */
@AnalyzeClasses(packages = "com.gresk.modules.agenda",
                importOptions = ImportOption.DoNotIncludeTests.class)
class AgendaModuleArchitectureTest {

    @ArchTest
    static final ArchRule elDominioNoDependeDeCapasExternas =
            noClasses().that().resideInAPackage("..modules.agenda.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..modules.agenda.application..",
                                        "..modules.agenda.infrastructure..");

    @ArchTest
    static final ArchRule laAplicacionNoDependeDeInfraestructura =
            noClasses().that().resideInAPackage("..modules.agenda.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..modules.agenda.infrastructure..");

    @ArchTest
    static final ArchRule elDominioNoUsaJpa =
            noClasses().that().resideInAPackage("..modules.agenda.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.persistence..", "org.hibernate..");
}
