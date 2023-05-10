plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.my.kizzy.feature_custom_rpc"
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
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
}

dependencies {
    implementation (libs.compose.ui)
    implementation (libs.material3)
    implementation (libs.material.icons.extended)
    implementation (libs.hilt)
    kapt (libs.hilt.compiler)
    implementation(libs.accompanist.permission)
    implementation(libs.gson)
    implementation(libs.activity.compose)
    implementation(libs.blankj.utilcodex)
    implementation(libs.glide)
    implementation(projects.common.components)
    implementation(projects.common.preference)
    implementation(projects.common.resources)
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.featureRpcBase)
    implementation(projects.featureProfile)
}