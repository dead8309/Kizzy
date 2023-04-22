@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}
android {
    compileSdk = 33
    namespace = "com.kizzy.color"
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    buildTypes {
        debug {
            isMinifyEnabled = true
        }
        release {
            isMinifyEnabled = true
        }
    }
}
dependencies {
    api(libs.compose.ui)
    api(libs.core.ktx)
    api(libs.material3)
}