plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("fibery.android.library")
}

android {
    resourcePrefix = "entity_details_"
    namespace = "com.krossovochkin.fiberyunofficial.entitydetails"
}

dependencies {
    api(libs.dagger)
    ksp(libs.dagger.compiler)
    api(libs.appcompat)
    api(libs.kotlinx.coroutines.core)
    api(project(":core:data:api"))
    api(project(":core:domain"))
    api(project(":core:presentation:ui:list"))
    api(project(":core:presentation:ui:toolbar"))

    implementation(libs.navigation.hilt)
    implementation(libs.io.noties.markwon.core)
    implementation(project(":core:presentation:resources"))
    implementation(project(":core:presentation:result"))
    implementation(project(":core:presentation:color"))
    implementation(project(":core:presentation:viewmodel"))

    implementation(libs.compose.foundation)
    implementation(libs.compose.lifecycle.viewmodel)
}
