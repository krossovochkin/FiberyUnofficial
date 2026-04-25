plugins {
    id("fibery.android.application")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.krossovochkin.fiberyunofficial"
    testBuildType = "debug"

    defaultConfig {
        applicationId = "com.krossovochkin.fiberyunofficial"
        testInstrumentationRunner = "com.krossovochkin.fiberyunofficial.FiberyTestRunner"
    }

    signingConfigs {
        register("release") {
            storeFile = file("$rootDir/release-keystore-fibery-unofficial.jks")
            storePassword = System.getenv()["RELEASE_STORE_PASSWORD"]
            keyAlias = System.getenv()["RELEASE_KEY_ALIAS"]
            keyPassword = System.getenv()["RELEASE_KEY_PASSWORD"]
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    kotlin {
        jvmToolchain(21)
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.runtime.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
        }
    }
}

dependencies {
    implementation(project(":core:data:api"))
    implementation(project(":core:data:auth"))
    implementation(project(":core:data:serialization"))
    implementation(project(":core:domain"))
    implementation(project(":core:presentation:result"))

    implementation(project(":feature:login"))
    implementation(project(":feature:app-list"))
    implementation(project(":feature:entity-type-list"))
    implementation(project(":feature:entity-list"))
    implementation(project(":feature:entity-details"))
    implementation(project(":feature:entity-create"))
    implementation(project(":feature:entity-create-domain"))
    implementation(project(":feature:picker-entity"))
    implementation(project(":feature:picker-single-select"))
    implementation(project(":feature:picker-multi-select"))
    implementation(project(":feature:picker-filter"))
    implementation(project(":feature:picker-sort"))
    implementation(project(":feature:file-list"))
    implementation(project(":feature:comment-list"))

    implementation(libs.io.noties.markwon.core)
    implementation(libs.paging.compose)
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)
    implementation(libs.converter.moshi)
    implementation(libs.navigation3.ui)
    implementation(libs.navigation3.runtime)
    implementation(libs.lifecycle.viewmodel.navigation3)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.foundation)
    implementation(libs.compose.activity)
    implementation(libs.compose.lifecycle.runtime)
    implementation(libs.compose.lifecycle.viewmodel)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.appcompat)
    implementation(libs.core.splashscreen)

    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.okhttp3)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)
}
