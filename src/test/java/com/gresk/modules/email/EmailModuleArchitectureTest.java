package com.gresk.modules.email;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Reglas de arquitectura hexagonal del módulo email:
 * el dominio no conoce las capas externas y la aplicación no conoce
 * la infraestructura (solo los puertos del dominio).
 */
@AnalyzeClasses(packages = "com.gresk.modules.email",
                importOptions = ImportOption.DoNotIncludeTests.class)
class EmailModuleArchitectureTest {

    @ArchTest
    static final ArchRule elDominioNoDependeDeCapasExternas =
            noClasses().that().resideInAPackage("..modules.email.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..modules.email.application..",
                                        "..modules.email.infrastructure..");

    @ArchTest
    static final ArchRule laAplicacionNoDependeDeInfraestructura =
            noClasses().that().resideInAPackage("..modules.email.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..modules.email.infrastructure..");

    @ArchTest
    static final ArchRule elDominioNoUsaJpa =
            noClasses().that().resideInAPackage("..modules.email.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.persistence..", "org.hibernate..");
}
