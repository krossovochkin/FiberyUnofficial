plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("fibery.android.library")
}

android {
    resourcePrefix = "picker_filter_"
    namespace = "com.krossovochkin.fiberyunofficial.pickerfilter"
}

dependencies {
    api(libs.dagger)
    ksp(libs.dagger.compiler)
    api(libs.kotlinx.coroutines.core)
    api(libs.appcompat)
    api(project(":core:data:api"))
    api(project(":core:domain"))
    api(project(":core:presentation:ui:list"))
    api(project(":core:presentation:ui:toolbar"))

    implementation(libs.navigation.hilt)
    implementation(project(":core:presentation:resources"))
    implementation(project(":core:presentation:result"))
    implementation(project(":core:presentation:system"))

    implementation(libs.compose.foundation)
    implementation(libs.compose.lifecycle.viewmodel)
}
