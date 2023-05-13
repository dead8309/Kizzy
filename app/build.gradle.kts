@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ApplicationDefaultConfig

plugins {
    id("kizzy.android.application")
    id("kizzy.android.application.compose")
    id("kizzy.android.hilt")
}

android {
    namespace = "com.my.kizzy"

    defaultConfig {
        applicationId = "com.my.kizzy"
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
        release {
            isShrinkResources = true
        }
    }

    packagingOptions.resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
}
dependencies {
    implementation (projects.gateway)
    implementation (projects.domain)
    implementation (projects.data)

    implementation (projects.theme)
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
    implementation (projects.common.preference)
    implementation (projects.common.navigation)

    // Extras
    implementation (libs.app.compat)
    implementation (libs.accompanist.navigation.animation)
    implementation (libs.gson)


    // Material
    implementation (libs.material3)
    implementation (libs.androidx.material)
    implementation (libs.material3.windows.size)

    implementation(libs.bundles.network.okhttp)
    implementation(libs.bundles.network.retrofit)
}

fun ApplicationDefaultConfig.buildConfigFieldFromGradleProperty(fieldName: String,gradlePropertyName: String) {
    val propertyValue = project.properties[gradlePropertyName] as? String
    checkNotNull(propertyValue) { "Gradle property $gradlePropertyName is null" }
    buildConfigField("String", fieldName, propertyValue)
}