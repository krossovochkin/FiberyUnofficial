plugins {
    id("fibery.android.library")
}

android {
    namespace = "com.krossovochkin.test.core"
}

dependencies {
    api(libs.kotlinx.coroutines.test)
    implementation(libs.truth)
}
