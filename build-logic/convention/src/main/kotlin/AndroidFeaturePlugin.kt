/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * AndroidFeaturePlugin.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("kizzy.android.library")
                apply("kizzy.android.hilt")
            }
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("implementation", project(":common:preference"))
                add("implementation", project(":common:components"))
                add("implementation", project(":common:resources"))

                add("implementation", project(":domain"))
                add("implementation", project(":data"))

                add("implementation", libs.findLibrary("material3").get())
            }
        }
    }
}