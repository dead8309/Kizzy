/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * KotlinAndroid.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package kizzy.gradle.plugin

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = 36

        defaultConfig {
            minSdk = 26
        }

        buildFeatures {
            buildConfig = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        buildTypes {
            getByName("release") {
                /*
                Starting with agp 8.4.0 setting isMinifyEnabled for libraries will minify the lib much sooner in the build process,
                as opposed to doing it at the end of the build process. This will cause the app module to use a minified
                version of the library which will throw missing classes error during compiling for release build.

                https://stackoverflow.com/a/79148742/25688500

                isMinifyEnabled = true
                 */

                /*
                cannot be used by libraries
                isShrinkResources = true
                */
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "${rootProject.rootDir.absolutePath}/app/proguard-rules.pro"
                )
            }
        }
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    }

}