@file:Suppress("UnstableApiUsage")

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
    implementation(project(":color"))
    implementation(project(":gateway"))
    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.compose_version}")
    implementation(AppDependencies.AppLibraries)
    kapt(AppDependencies.HiltCompiler)
}

fun ApplicationDefaultConfig.buildConfigFieldFromGradleProperty(fieldName: String,gradlePropertyName: String) {
    val propertyValue = project.properties[gradlePropertyName] as? String
    checkNotNull(propertyValue) { "Gradle property $gradlePropertyName is null" }

    buildConfigField("String", fieldName, propertyValue)
}