plugins {
    id("fibery.android.library")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.krossovochkin.fiberyunofficial.domain"
}

dependencies {
    api(libs.navigation3.runtime)
    api(libs.threetenabp)
    implementation(project(":core:data:serialization"))
    implementation(libs.converter.moshi)
    ksp(libs.moshi.kotlin.codegen)
}
