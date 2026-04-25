plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("fibery.android.library")
}

android {
    resourcePrefix = "picker_single_select_"
    namespace = "com.krossovochkin.fiberyunofficial.pickersingleselect"
}

dependencies {
    api(libs.dagger)
    ksp(libs.dagger.compiler)
    api(project(":core:domain"))
    implementation(libs.navigation.hilt)
    implementation(libs.appcompat)
    implementation(project(":core:presentation:result"))
    implementation(libs.compose.foundation)
    implementation(libs.compose.lifecycle.viewmodel)
}
