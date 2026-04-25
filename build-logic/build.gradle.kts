plugins {
    `kotlin-dsl`
}

group = "com.krossovochkin.fiberyunofficial.buildlogic"

dependencies {
    implementation(libs.gradle.plugin.android)
    implementation(libs.gradle.plugin.kotlin)
    implementation(libs.gradle.plugin.compose.compiler)
}
