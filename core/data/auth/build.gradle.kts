plugins {
    id("fibery.android.library")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.krossovochkin.auth"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.tink.android)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)
}
