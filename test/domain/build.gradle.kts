plugins {
    id("fibery.android.library")
}

android {
    namespace = "com.krossovochkin.fiberyfunofficial.test.domain"
}

dependencies {
    api(project(":core:domain"))
}
