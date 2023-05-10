plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.my.kizzy.feature_crash_handler"
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
    implementation(libs.crashx)
    implementation (libs.blankj.utilcodex)
    implementation (libs.compose.ui)
    implementation (libs.compose.ui.tooling.preview)
    implementation (libs.compose.ui.tooling)
    implementation (libs.material.icons.extended)
    implementation (libs.material3)
    implementation (projects.theme)
    implementation (projects.common.resources)
    implementation (projects.common.preference)
}