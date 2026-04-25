plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("fibery.android.library")
}

android {
    resourcePrefix = "file_list"
    namespace = "com.krossovochkin.filelist"
}

dependencies {
    api(libs.dagger)
    ksp(libs.dagger.compiler)
    api(libs.kotlinx.coroutines.core)
    api(libs.appcompat)
    api(project(":core:data:api"))
    api(project(":core:domain"))
    api(project(":core:data:auth"))
    api(project(":core:presentation:ui:toolbar"))

    implementation(libs.navigation.hilt)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.paging.compose)
    implementation(project(":core:presentation:resources"))
    implementation(project(":core:presentation:ui:paging"))
    implementation(project(":core:presentation:result"))
    implementation(project(":core:presentation:color"))

    implementation(libs.compose.foundation)
    implementation(libs.compose.lifecycle.viewmodel)
}
