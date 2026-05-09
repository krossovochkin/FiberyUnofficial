plugins {
    id("fibery.android.library")
}

android {
    namespace = "com.krossovochkin.fiberyunofficial.ui.list"
}

dependencies {
    implementation(project(":core:presentation:viewmodel"))
}
