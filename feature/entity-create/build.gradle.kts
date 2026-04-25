plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("fibery.android.library")
}

android {
    resourcePrefix = "entity_create_"
    namespace = "com.krossovochkin.fiberyunofficial.entitycreate"
}

dependencies {
    api(libs.dagger)
    ksp(libs.dagger.compiler)
    api(libs.kotlinx.coroutines.core)
    api(libs.appcompat)
    api(libs.material)
    api(project(":core:domain"))
    api(project(":core:presentation:ui:toolbar"))
    api(project(":feature:entity-create-domain"))

    implementation(libs.navigation.hilt)
    implementation(project(":core:presentation:resources"))
    implementation(project(":core:presentation:result"))
    implementation(project(":core:presentation:system"))

    implementation(libs.compose.foundation)
    implementation(libs.compose.lifecycle.viewmodel)
}
