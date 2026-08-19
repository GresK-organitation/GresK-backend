package com.gresk.modules.musicdna;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Reglas de arquitectura hexagonal del módulo musicdna:
 * el dominio no conoce las capas externas y la aplicación no conoce
 * la infraestructura (solo los puertos del dominio/aplicación).
 */
@AnalyzeClasses(packages = "com.gresk.modules.musicdna",
                importOptions = ImportOption.DoNotIncludeTests.class)
class MusicDnaModuleArchitectureTest {

    @ArchTest
    static final ArchRule elDominioNoDependeDeCapasExternas =
            noClasses().that().resideInAPackage("..modules.musicdna.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..modules.musicdna.application..",
                                        "..modules.musicdna.infrastructure..");

    @ArchTest
    static final ArchRule laAplicacionNoDependeDeInfraestructura =
            noClasses().that().resideInAPackage("..modules.musicdna.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..modules.musicdna.infrastructure..");

    @ArchTest
    static final ArchRule elDominioNoUsaJpa =
            noClasses().that().resideInAPackage("..modules.musicdna.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.persistence..", "org.hibernate..");
}
