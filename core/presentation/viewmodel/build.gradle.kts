plugins {
    id("fibery.android.library")
}

android {
    namespace = "com.krossovochkin.viewmodel"
}

dependencies {
    api(libs.lifecycle.viewmodel.ktx)
    api(libs.kotlinx.coroutines.core)
}
