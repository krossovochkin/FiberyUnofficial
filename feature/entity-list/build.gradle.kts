plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("fibery.android.library")
}

android {
    resourcePrefix = "entity_list_"
    namespace = "com.krossovochkin.fiberyunofficial.entitylist"
}

dependencies {
    api(libs.dagger)
    ksp(libs.dagger.compiler)
    api(libs.kotlinx.coroutines.core)
    api(libs.appcompat)
    api(libs.material)
    api(project(":core:data:api"))
    api(project(":core:domain"))
    api(project(":core:data:serialization"))
    api(project(":core:presentation:ui:toolbar"))
    api(project(":core:presentation:ui:fab"))

    implementation(libs.navigation.hilt)
    implementation(libs.paging.compose)
    implementation(project(":core:presentation:resources"))
    implementation(project(":core:presentation:ui:paging"))
    implementation(project(":core:presentation:result"))
    implementation(project(":core:presentation:color"))

    implementation(libs.compose.foundation)
    implementation(libs.compose.lifecycle.viewmodel)
}
