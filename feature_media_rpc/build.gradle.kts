plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.my.kizzy.feature_media_rpc"
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
    composeOptions.kotlinCompilerExtensionVersion= libs.versions.compose.compiler.get()
}

dependencies {
    implementation(projects.featureRpcBase)
    implementation(projects.common.components)
    implementation(projects.common.preference)
    implementation(projects.common.strings)
    implementation(libs.compose.ui)
    implementation(libs.material.icons.extended)
    implementation(libs.material3)
}