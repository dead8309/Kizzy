plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.my.kizzy.feature_profile"
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
    implementation (libs.material3)
    implementation (libs.compose.ui)
    implementation (projects.common.preference)
    implementation (projects.common.strings)
    implementation (projects.common.components)
    implementation (projects.theme)
    implementation (projects.domain)
    implementation (projects.gateway)
    implementation (libs.glide)
    debugImplementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation (libs.hilt)
    implementation (libs.gson)
    implementation(libs.activity.compose)
}