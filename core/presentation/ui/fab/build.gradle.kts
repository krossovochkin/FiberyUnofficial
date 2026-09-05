plugins {
    id("fibery.android.library")
}

android {
    namespace = "com.krossovochkin.core.presentation.ui.fab"
}

dependencies {
    api(project(":core:presentation:resources"))
}
