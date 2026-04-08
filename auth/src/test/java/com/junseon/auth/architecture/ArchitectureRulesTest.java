package com.junseon.auth.architecture;

import com.junseon.auth.social.SocialIdentityVerifier;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.junseon.auth", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureRulesTest {

    @ArchTest
    static final ArchRule socialContractsMustNotDependOnProviderImplementations = noClasses()
            .that().resideInAPackage("com.junseon.auth.social")
            .should().dependOnClassesThat().resideInAnyPackage("..social.*..")
            .because("social root contracts must stay provider-agnostic");

    @ArchTest
    static final ArchRule authApplicationMustNotDependOnProviderImplementations = noClasses()
            .that().resideInAPackage("..auth.application..")
            .should().dependOnClassesThat().resideInAnyPackage("..social.*..")
            .because("auth application must depend on social contracts, not concrete provider implementations");

    @ArchTest
    static final ArchRule providerImplementationsMustImplementSocialIdentityVerifier = classes()
            .that().resideInAPackage("..social.*..")
            .and().areAnnotatedWith(Component.class)
            .should().implement(SocialIdentityVerifier.class)
            .because("provider implementation entry points must implement social contracts");

    @ArchTest
    static final ArchRule providerImplementationsMustNotDependOnAuthOrUserImplementations = noClasses()
            .that().resideInAPackage("..social.*..")
            .should().dependOnClassesThat().resideInAnyPackage("..auth.presentation..", "..auth.application.impl..", "..user.infrastructure..")
            .because("provider implementation must stay isolated from auth/user concrete internals");
}
