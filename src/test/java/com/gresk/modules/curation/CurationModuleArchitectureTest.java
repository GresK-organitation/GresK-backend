package com.gresk.modules.curation;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Reglas de arquitectura hexagonal del módulo curation:
 * el dominio no conoce las capas externas y la aplicación no conoce
 * la infraestructura (solo los puertos del dominio).
 */
@AnalyzeClasses(packages = "com.gresk.modules.curation",
                importOptions = ImportOption.DoNotIncludeTests.class)
class CurationModuleArchitectureTest {

    @ArchTest
    static final ArchRule elDominioNoDependeDeCapasExternas =
            noClasses().that().resideInAPackage("..modules.curation.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..modules.curation.application..",
                                        "..modules.curation.infrastructure..");

    @ArchTest
    static final ArchRule laAplicacionNoDependeDeInfraestructura =
            noClasses().that().resideInAPackage("..modules.curation.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..modules.curation.infrastructure..");

    @ArchTest
    static final ArchRule elDominioNoUsaJpa =
            noClasses().that().resideInAPackage("..modules.curation.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.persistence..", "org.hibernate..");
}
