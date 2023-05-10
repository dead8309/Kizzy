plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.my.kizzy.feature_about"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        buildConfigField("String","VERSION_NAME", "\"${libs.versions.version.name.get()}\"")
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
    implementation (projects.common.resources)
    implementation (projects.common.components)
    implementation (projects.domain)
    implementation (libs.glide)
    implementation (libs.material.icons.extended)
    implementation (libs.compose.ui)
    implementation (libs.material3)
    implementation (libs.hilt)
    kapt(libs.hilt.compiler)
    implementation (libs.activity.compose)
    debugImplementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
}
