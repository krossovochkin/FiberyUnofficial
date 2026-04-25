plugins {
    id("fibery.android.library")
}

android {
    namespace = "com.krossovochkin.core.presentation.resources"
}

dependencies {
    implementation(project(":core:presentation:color"))
    implementation(libs.core.ktx)
}
