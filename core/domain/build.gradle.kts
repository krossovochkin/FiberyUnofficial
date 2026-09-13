plugins {
    id("fibery.android.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.krossovochkin.fiberyunofficial.domain"
}

dependencies {
    api(libs.navigation3.runtime)
    api(libs.threetenabp)
    implementation(project(":core:data:serialization"))
}
