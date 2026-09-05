plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("fibery.android.library")
}

android {
    resourcePrefix = "picker_sort_"
    namespace = "com.krossovochkin.fiberyunofficial.pickersort"
}

dependencies {
    api(libs.dagger)
    ksp(libs.dagger.compiler)
    api(libs.kotlinx.coroutines.core)
    api(project(":core:domain"))
    api(project(":core:presentation:ui:list"))
    api(project(":core:presentation:ui:toolbar"))

    implementation(project(":core:presentation:resources"))

    implementation(libs.compose.foundation)
    implementation(libs.compose.lifecycle.viewmodel)
}
