@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ApplicationDefaultConfig

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.my.kizzy"

    compileSdk = 33
    defaultConfig {
        applicationId = "com.my.kizzy"
        minSdk = 26
        targetSdk = 33
        versionCode = libs.versions.version.code.get().toInt()
        versionName = libs.versions.version.name.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigFieldFromGradleProperty("BASE_URL","BASE_URL")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
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
    packagingOptions.resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
}
dependencies {
    implementation(projects.color)
    implementation(projects.gateway)
    implementation(projects.common.preference)
    implementation(projects.common.strings)
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.common.components)
    implementation(projects.theme)
    implementation (projects.featureStartup)
    implementation (projects.featureCrashHandler)
    implementation (projects.featureProfile)
    implementation (projects.featureAbout)
    implementation (projects.featureSettings)
    implementation (projects.featureLogs)
    implementation (projects.featureRpcBase)
    implementation (projects.featureAppsRpc)
    implementation (projects.featureMediaRpc)
    implementation (projects.featureConsoleRpc)
    implementation (projects.featureCustomRpc)
    implementation (projects.featureHome)
    implementation (projects.common.navigation)
    implementation (libs.hilt.navigation)

    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui)
    kapt(libs.hilt.compiler)
    implementation(libs.coil)
    implementation(libs.coil.svg)
    implementation(libs.android.svg)
    implementation(libs.app.compat)
    implementation(libs.core.ktx)
    implementation(libs.material3)
    implementation(libs.material3.windows.size)
    implementation(libs.lifecycle.runtime)
    implementation(libs.activity.compose)
    implementation(libs.androidx.material)
    implementation(libs.compose.navigation)
    implementation(libs.material.icons.extended)
    implementation(libs.gson)
    implementation(libs.glide)
    implementation(libs.blankj.utilcodex)
    implementation(libs.crashx)
    implementation(libs.hilt)
    implementation(libs.bundles.compose.accompanist)
    implementation(libs.bundles.network.okhttp)
    implementation(libs.bundles.network.retrofit)
}

fun ApplicationDefaultConfig.buildConfigFieldFromGradleProperty(fieldName: String,gradlePropertyName: String) {
    val propertyValue = project.properties[gradlePropertyName] as? String
    checkNotNull(propertyValue) { "Gradle property $gradlePropertyName is null" }
    buildConfigField("String", fieldName, propertyValue)
}