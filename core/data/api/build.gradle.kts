plugins {
    id("fibery.android.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.krossovochkin.fiberyunofficial.api"
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization.json)
    api(libs.retrofit)
    api(project(":core:data:serialization"))
    api(project(":core:domain"))
}
