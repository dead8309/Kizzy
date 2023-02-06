@file:Suppress("UnstableApiUsage")

/*
*
*  ******************************************************************
*  *  * Copyright (C) 2022
*  *  * build.gradle.kts is part of Kizzy
*  *  *  and can not be copied and/or distributed without the express
*  *  * permission of yzziK(Vaibhav)
*  *  *****************************************************************
*
*
*/
import com.android.build.api.dsl.ApplicationDefaultConfig

plugins {
    id("com.android.application")
    kotlin("android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.my.kizzy"

    compileSdk = AppConfig.compileSdk
    defaultConfig {
        applicationId = "com.my.kizzy"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigFieldFromGradleProperty("BASE_URL","BASE_URL")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}
dependencies {
    debugImplementation(project(":bubble-logger"))
    releaseImplementation(project(":bubble-logger-no-op"))
    implementation(project(":color"))
    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.compose_version}")
    implementation(AppDependencies.appLibraries)
    kapt(AppDependencies.HiltCompiler)
}
/*
Takes value from Gradle project property and sets it as Android build config property eg.
apiToken variable present in the settings.gradle file will be accessible as BuildConfig.GRADLE_API_TOKEN in the app.
*/
fun ApplicationDefaultConfig.buildConfigFieldFromGradleProperty(fieldName: String,gradlePropertyName: String) {
    val propertyValue = project.properties[gradlePropertyName] as? String
    checkNotNull(propertyValue) { "Gradle property $gradlePropertyName is null" }

    buildConfigField("String", fieldName, propertyValue)
}