plugins {
    id("fibery.android.library")
}

android {
    namespace = "com.krossovochkin.fiberyunofficial.ui.paging"
}

dependencies {
    api(libs.paging.runtime.ktx)
    api(project(":core:presentation:ui:list"))
}
