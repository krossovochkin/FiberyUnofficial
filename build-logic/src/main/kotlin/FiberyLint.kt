import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

fun Project.configureLint() {
    plugins.withId("com.android.application") {
        extensions.configure<ApplicationExtension> {
            lint.configure()
        }
    }
    plugins.withId("com.android.library") {
        extensions.configure<LibraryExtension> {
            lint.configure()
        }
    }
}

private fun Lint.configure() {
    abortOnError = true
    htmlReport = true
    warningsAsErrors = true
    xmlReport = false
    disable += listOf(
        "UnknownIssueId",
        "ObsoleteLintCustomCheck",
        "UnsafeExperimentalUsageError",
        "UnsafeExperimentalUsageWarning",

        "ConvertToWebp",
        "Overdraw",
        "UnusedIds",
        "SelectableText",
        "UnusedResources",
        "StopShip",
        "DuplicateStrings",

        "MonochromeLauncherIcon",
        "GradleDependency",
        "AndroidGradlePluginVersion"
    )
}
