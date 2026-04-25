plugins {
    id("fibery.android.library")
}

android {
    namespace = "com.krossovochkin.fiberyunofficial.api"
}

dependencies {
    api(project(":core:data:api"))
    api(project(":core:domain"))
}
