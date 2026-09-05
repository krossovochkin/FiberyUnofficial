plugins {
    id("fibery.android.library")
}

android {
    namespace = "com.krossovochkin.core.presentation.ui.toolbar"
}

dependencies {
    api(project(":core:presentation:resources"))
}
