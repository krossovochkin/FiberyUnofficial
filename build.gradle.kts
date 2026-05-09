plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.gradle.plugin.android)
        classpath(libs.gradle.plugin.kotlin)
        classpath(libs.gradle.plugin.ksp)
        classpath(libs.gradle.plugin.kotlin.serialization)
        classpath(libs.gradle.plugin.compose.compiler)
        classpath(libs.gradle.navigation.safe.args)
        classpath(libs.gradle.hilt)
        classpath(libs.gradle.plugin.detekt)
        classpath(libs.kover)
    }
}

dependencies {
    "detektPlugins"(libs.detekt.formatting)
}

tasks.register<io.gitlab.arturbosch.detekt.Detekt>("detektAll") {
    allRules = true
    parallel = true
    setSource(files(projectDir))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
    buildUponDefaultConfig = true
    config.from(files("$rootDir/quality/detekt.yml"))
    autoCorrect = true
    reports {
        html.required.set(false)
        xml.required.set(false)
        txt.required.set(false)
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
