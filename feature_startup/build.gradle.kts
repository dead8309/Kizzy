plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.my.kizzy.feature_startup"
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
    implementation (libs.activity.compose)
    implementation (libs.compose.ui)
    implementation(libs.material3)
    implementation (libs.material.icons.extended)
    implementation(projects.common.strings)
    implementation (libs.accompanist.permission)
    implementation(projects.data)
    implementation(projects.domain)
    implementation(libs.gson)
    implementation(projects.common.preference)
    implementation(projects.common.components)
}