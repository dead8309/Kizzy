import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = "kizzy.android.application.compose"
            implementationClass = "AndroidApplicationComposePlugin"
        }
        register("androidApplication") {
            id = "kizzy.android.application"
            implementationClass = "AndroidApplicationPlugin"
        }
        register("androidLibraryCompose") {
            id = "kizzy.android.library.compose"
            implementationClass = "AndroidLibraryComposePlugin"
        }
        register("androidLibrary") {
            id = "kizzy.android.library"
            implementationClass = "AndroidLibraryPlugin"
        }
        register("androidFeature") {
            id = "kizzy.android.feature"
            implementationClass = "AndroidFeaturePlugin"
        }
        register("androidHilt") {
            id = "kizzy.android.hilt"
            implementationClass = "AndroidHiltPlugin"
        }
    }
}