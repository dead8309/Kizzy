package kizzy.gradle.plugin

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *>,
) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("compose.compiler").get().toString()
        }

        dependencies {
            val composeUi = libs.findLibrary("compose.ui").get()
            val composeUiTooling = libs.findLibrary("compose.ui.tooling").get()
            val composeUiToolingPreview = libs.findLibrary("compose-ui.tooling.preview").get()
            add("implementation",composeUi)
            add("debugImplementation",composeUiTooling)
            add("implementation",composeUiToolingPreview)
        }
    }
}