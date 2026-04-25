plugins {
    id("fibery.android.library")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    resourcePrefix = "app_list_"
    namespace = "com.krossovochkin.fiberyunofficial.applist"
}

dependencies {
    implementation(libs.compose.foundation)
    implementation(libs.compose.lifecycle.viewmodel)

    api(libs.kotlinx.coroutines.core)
    api(libs.appcompat)
    api(libs.dagger)
    ksp(libs.dagger.compiler)
    api(project(":core:data:api"))
    api(project(":core:domain"))
    api(project(":core:presentation:ui:list"))
    api(project(":core:presentation:ui:toolbar"))

    implementation(libs.navigation.hilt)
    implementation(project(":core:presentation:resources"))
    implementation(project(":core:presentation:result"))

    testImplementation(libs.junit.junit)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.kotlin)
    testImplementation(project(":test:core"))
    testImplementation(project(":test:domain"))
    testImplementation(project(":test:api"))
}
