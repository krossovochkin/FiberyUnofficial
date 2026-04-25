plugins {
    id("fibery.android.library")
}

android {
    namespace = "com.krossovochkin.core.presentation.ui.fab"
}

dependencies {
    api(libs.fragment.ktx)
    api(libs.material)
    api(project(":core:presentation:resources"))
    implementation(project(":core:presentation:system"))
    implementation(project(":core:presentation:color"))
}
