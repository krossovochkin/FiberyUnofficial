import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.jetbrains.kotlin.plugin.compose")
}

configureLint()

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

extensions.configure<LibraryExtension> {
    compileSdk = libs.findVersion("android-compile-sdk").get().requiredVersion.toInt()

    defaultConfig {
        minSdk = libs.findVersion("android-min-sdk").get().requiredVersion.toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val consumerProguard = file("consumer-rules.pro")
        if (consumerProguard.exists()) {
            consumerProguardFiles(consumerProguard)
        }
    }

    lint {
        targetSdk = libs.findVersion("android-target-sdk").get().requiredVersion.toInt()
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true

            val proguard = file("proguard-rules.pro")
            if (proguard.exists()) {
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), proguard)
            } else {
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            }
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    val bom = libs.findLibrary("compose-bom").get()
    add("implementation", platform(bom))
    add("implementation", libs.findLibrary("compose-runtime").get())
    add("implementation", libs.findLibrary("compose-ui").get())
    add("implementation", libs.findLibrary("compose-material3").get())
    add("implementation", libs.findLibrary("compose-material-icons-extended").get())
}
