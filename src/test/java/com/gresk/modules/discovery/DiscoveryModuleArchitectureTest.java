package com.gresk.modules.discovery;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Reglas de arquitectura hexagonal del módulo discovery: el dominio no
 * conoce las capas externas y la aplicación no conoce la infraestructura.
 */
@AnalyzeClasses(packages = "com.gresk.modules.discovery",
                importOptions = ImportOption.DoNotIncludeTests.class)
class DiscoveryModuleArchitectureTest {

    @ArchTest
    static final ArchRule elDominioNoDependeDeCapasExternas =
            noClasses().that().resideInAPackage("..modules.discovery.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..modules.discovery.application..",
                                        "..modules.discovery.infrastructure..");

    @ArchTest
    static final ArchRule laAplicacionNoDependeDeInfraestructura =
            noClasses().that().resideInAPackage("..modules.discovery.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..modules.discovery.infrastructure..");

    @ArchTest
    static final ArchRule elDominioNoUsaJpa =
            noClasses().that().resideInAPackage("..modules.discovery.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.persistence..", "org.hibernate..");
}
