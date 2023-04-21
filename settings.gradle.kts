@file:Suppress("UnstableApiUsage")

include(":color")


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

include(":app", ":gateway")
rootProject.name = "Kizzy"
