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

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.kizzy.bubble_logger"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = 29
        targetSdk = AppConfig.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(AppDependencies.loggerNoOpLibraries)
}