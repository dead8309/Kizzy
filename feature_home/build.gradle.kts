plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.my.kizzy.feature_home"
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
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
}

dependencies {
    implementation (libs.compose.ui)
    debugImplementation (libs.compose.ui.tooling)
    debugImplementation (libs.compose.ui.tooling.preview)
    implementation (libs.gson)
    implementation (libs.material3)
    implementation (libs.accompanist.flowLayout)
    implementation (projects.domain)
    implementation (projects.data)
    implementation (projects.featureRpcBase)
    implementation (projects.featureSettings)
    implementation (projects.common.preference)
    implementation (projects.common.components)
    implementation (projects.common.strings)
    implementation (libs.glide)
}