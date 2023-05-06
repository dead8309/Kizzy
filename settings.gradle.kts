@file:Suppress("UnstableApiUsage")
/*
*
*  ******************************************************************
*  *  * Copyright (C) 2022
*  *  * settings.gradle.kts is part of Kizzy
*  *  *  and can not be copied and/or distributed without the express
*  *  * permission of yzziK(Vaibhav)
*  *  *****************************************************************
*
*
*/
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
    ":theme"
)
rootProject.name = "Kizzy"
