package com.gresk.modules.finance;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Reglas de arquitectura hexagonal del módulo finance:
 * el dominio no conoce las capas externas y la aplicación no conoce
 * la infraestructura (solo los puertos del dominio).
 */
@AnalyzeClasses(packages = "com.gresk.modules.finance",
                importOptions = ImportOption.DoNotIncludeTests.class)
class FinanceModuleArchitectureTest {

    @ArchTest
    static final ArchRule elDominioNoDependeDeCapasExternas =
            noClasses().that().resideInAPackage("..modules.finance.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..modules.finance.application..",
                                        "..modules.finance.infrastructure..");

    @ArchTest
    static final ArchRule laAplicacionNoDependeDeInfraestructura =
            noClasses().that().resideInAPackage("..modules.finance.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..modules.finance.infrastructure..");

    @ArchTest
    static final ArchRule elDominioNoUsaJpa =
            noClasses().that().resideInAPackage("..modules.finance.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.persistence..", "org.hibernate..");
}
