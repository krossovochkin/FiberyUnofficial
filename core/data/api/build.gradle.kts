plugins {
    id("fibery.android.library")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.krossovochkin.fiberyunofficial.api"
}

dependencies {
    api(libs.retrofit)
    api(project(":core:data:serialization"))
    api(project(":core:domain"))
    api(libs.converter.moshi)
    ksp(libs.moshi.kotlin.codegen)
}
