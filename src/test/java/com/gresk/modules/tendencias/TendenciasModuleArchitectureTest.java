package com.gresk.modules.tendencias;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Reglas de arquitectura del sub-hexágono chronicle (el sub-hexágono stats no
 * tiene domain/ propio a propósito, es un slice de solo-lectura/agregación).
 */
@AnalyzeClasses(packages = "com.gresk.modules.tendencias.chronicle",
                importOptions = ImportOption.DoNotIncludeTests.class)
class TendenciasModuleArchitectureTest {

    @ArchTest
    static final ArchRule elDominioDeChronicleNoDependeDeCapasExternas =
            noClasses().that().resideInAPackage("..tendencias.chronicle.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..tendencias.chronicle.application..",
                                        "..tendencias.chronicle.infrastructure..");

    @ArchTest
    static final ArchRule laAplicacionDeChronicleNoDependeDeInfraestructura =
            noClasses().that().resideInAPackage("..tendencias.chronicle.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..tendencias.chronicle.infrastructure..");

    @ArchTest
    static final ArchRule elDominioDeChronicleNoUsaJpa =
            noClasses().that().resideInAPackage("..tendencias.chronicle.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.persistence..", "org.hibernate..");
}
