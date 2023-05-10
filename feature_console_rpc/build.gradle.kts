plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.my.kizzy.feature_console_rpc"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
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
    implementation (projects.featureRpcBase)
    implementation (libs.hilt)
    kapt (libs.hilt.compiler)
    implementation (libs.compose.ui)
    implementation (libs.material3)
    implementation (libs.material.icons.extended)
    implementation (libs.gson)
    implementation (libs.glide)
    implementation (projects.common.components)
    implementation (projects.common.preference)
    implementation (projects.common.resources)
    implementation (projects.domain)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.tooling.preview)
    implementation (libs.activity.compose)
}