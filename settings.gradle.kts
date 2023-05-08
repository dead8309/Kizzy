@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(
    ":app",
    ":gateway",
    ":color",
    ":common:preference",
    ":common:strings",
    ":domain",
    ":data",
    ":common:components",
    ":theme",
    ":feature_crash_handler",
    ":feature_startup",
    ":feature_profile",
    ":feature_about",
    ":feature_settings",
    ":feature_logs"
)
rootProject.name = "Kizzy"
