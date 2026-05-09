plugins {
    id("fibery.android.library")
}

android {
    namespace = "com.krossovochkin.core.presentation.system"
}

dependencies {
    implementation(project(":core:presentation:color"))
    implementation(libs.insetter)
}
