plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("fibery.android.library")
}

android {
    resourcePrefix = "login_"
    namespace = "com.krossovochkin.fiberyunofficial.login"
}

dependencies {
    api(libs.dagger)
    ksp(libs.dagger.compiler)
    api(libs.kotlinx.coroutines.core)
    api(project(":core:data:auth"))
    implementation(project(":core:presentation:resources"))
    implementation(project(":core:presentation:result"))

    implementation(libs.compose.foundation)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.compose.lifecycle.viewmodel)
}
