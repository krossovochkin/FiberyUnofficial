plugins {
    id("fibery.android.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.krossovochkin.serialization"
}

dependencies {
    api(libs.kotlinx.serialization.json)
    api(libs.threetenabp)
}
