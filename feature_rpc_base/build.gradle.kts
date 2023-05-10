plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.my.kizzy.feature_rpc_base"
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
}

dependencies {
    implementation (libs.hilt)
    kapt(libs.hilt.compiler)
    implementation (libs.gson)
    implementation (libs.blankj.utilcodex)
    implementation (projects.common.preference)
    implementation (projects.featureSettings)
    implementation (projects.domain)
    implementation (projects.data)
    implementation(libs.androidx.material)
    implementation (libs.compose.ui)

}