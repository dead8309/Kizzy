plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.my.kizzy.ui.components"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation (libs.compose.ui)
    implementation (libs.material3)
    implementation (projects.color)
    debugImplementation(libs.compose.ui.tooling.preview)
    implementation (libs.android.svg)
    implementation (libs.coil)
    implementation(projects.common.strings)
    implementation (libs.material.icons.extended)
}