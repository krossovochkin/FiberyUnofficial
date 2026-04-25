plugins {
    id("fibery.android.library")
}

android {
    namespace = "com.krossovochkin.core.presentation.result"
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.dagger)
}
