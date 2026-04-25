pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.6"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FiberyUnofficial"

includeBuild("build-logic")

include(":app")
include(":core:domain")
include(":core:data:serialization")
include(":core:data:api")
include(":core:data:auth")
include(":core:presentation:color")
include(":core:presentation:ui:paging")
include(":core:presentation:ui:list")
include(":core:presentation:viewmodel")
include(":core:presentation:system")
include(":core:presentation:resources")
include(":core:presentation:result")
include(":core:presentation:ui:toolbar")
include(":core:presentation:ui:fab")
include(":feature:app-list")
include(":feature:entity-type-list")
include(":feature:entity-list")
include(":feature:entity-details")
include(":feature:login")
include(":feature:entity-create")
include(":feature:entity-create-domain")
include(":feature:picker-entity")
include(":feature:picker-single-select")
include(":feature:picker-multi-select")
include(":feature:picker-filter")
include(":feature:picker-sort")
include(":feature:file-list")
include(":feature:comment-list")
include(":test:domain")
include(":test:api")
include(":test:core")
