plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.my.kizzy.feature_settings"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        buildConfigField("String","VERSION_NAME", "\"${libs.versions.version.name.get()}\"")
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
    implementation(libs.compose.ui)
    implementation(libs.material3)
    implementation(libs.androidx.material)
    implementation(libs.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.glide)
    implementation(libs.gson)
    implementation(libs.accompanist.pager.layouts)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.coil)
    implementation(libs.coil.svg)
    implementation(libs.android.svg)

    implementation(projects.common.resources)
    implementation(projects.common.components)
    implementation(projects.common.preference)
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.color)
    implementation(projects.theme)
}