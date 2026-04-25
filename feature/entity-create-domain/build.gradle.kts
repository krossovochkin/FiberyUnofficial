plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("fibery.android.library")
}

android {
    namespace = "com.krossovochkin.fiberyunofficial.entitycreatedomain"
}

dependencies {
    api(libs.dagger)
    ksp(libs.dagger.compiler)
    api(project(":core:data:api"))
    api(project(":core:domain"))
}
