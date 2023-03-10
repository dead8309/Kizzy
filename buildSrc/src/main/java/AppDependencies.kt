/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * AppDependencies.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

import org.gradle.api.artifacts.dsl.DependencyHandler

object AppDependencies {
    //Core
    const val coreKtx = "androidx.core:core-ktx:${Versions.ktxVersion}"
    const val material3 = "androidx.compose.material3:material3:${Versions.material3Version}"
    const val windowSizeClass = "androidx.compose.material3:material3-window-size-class:${Versions.material3Version}"
    const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.ktxLifecycleVersion}"
    const val androidxMaterial = "com.google.android.material:material:${Versions.androidxMaterialVersion}"

    const val KotlinXCoroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.KotlinXCoroutine}"

    const val composeActivity = "androidx.activity:activity-compose:${Versions.activityComposeVersion}"
    const val Hilt = "com.google.dagger:hilt-android:${Versions.hiltVersion}"
    const val HiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hiltVersion}"
    const val AppCompat = "androidx.appcompat:appcompat:1.7.0-alpha01"

    //Compose
    const val composeUi = "androidx.compose.ui:ui:${Versions.compose_version}"
    const val composeToolingPreview ="androidx.compose.ui:ui-tooling-preview:${Versions.compose_version}"
    const val materialIconsExtended = "androidx.compose.material:material-icons-extended:${Versions.compose_version}"
    const val composeNavigation = "androidx.navigation:navigation-compose:${Versions.composeNavigationVersion}"

    //Accompanist
    const val accompanistPermission = "com.google.accompanist:accompanist-permissions:${Versions.accompanistPermissionVersion}"
    const val accompanistAnimation ="com.google.accompanist:accompanist-navigation-animation:${Versions.accompanistVersion}"
    const val accompanistSystemUiController = "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanistVersion}"
    const val accompanistFlowLayout = "com.google.accompanist:accompanist-flowlayout:0.28.0"

    //Utilities
    const val googleGson = "com.google.code.gson:gson:${Versions.gsonVersion}"
    const val glide = "com.github.skydoves:landscapist-glide:${Versions.glideVersion}"
    const val blankjUtil = "com.blankj:utilcodex:${Versions.utilcodeVersion}"
    const val okhttp =  "com.squareup.okhttp3:okhttp:${Versions.okhttpVersion}"
    const val okhttpInterceptor =  "com.squareup.okhttp3:logging-interceptor:${Versions.okhttpVersion}"
    const val crashX = "com.github.TutorialsAndroid:crashx:v6.0.19"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofitVersion}"
    const val retrofitGsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofitVersion}"
    const val MMKV = "com.tencent:mmkv:${Versions.mmkvVersion}"
    const val coilCompose = "io.coil-kt:coil-compose:${Versions.coilVersion}"
    const val coilSvg = "io.coil-kt:coil-svg:${Versions.coilVersion}"
    const val androidSVG = "com.caverock:androidsvg-aar:1.4"

    //Ktor
    const val ktorCore = "io.ktor:ktor-client-core:${Versions.ktorVersion}"
    const val ktorClientCIO = "io.ktor:ktor-client-cio:${Versions.ktorVersion}"
    const val ktorWebSockets = "io.ktor:ktor-client-websockets:${Versions.ktorVersion}"
    const val ktorGson = "io.ktor:ktor-serialization-gson:${Versions.ktorVersion}"

    val AppLibraries = arrayListOf<String>().apply {
        add(coilCompose)
        add(coilSvg)
        add(androidSVG)
        add(AppCompat)
        add(MMKV)
        add(coreKtx)
        add(material3)
        add(windowSizeClass)
        add(lifecycleRuntimeKtx)
        add(composeActivity)
        add(composeUi)
        add(composeToolingPreview)
        add(androidxMaterial)
        add(composeNavigation)
        add(materialIconsExtended)
        add(googleGson)
        add(glide)
        add(blankjUtil)
        add(okhttp)
        add(accompanistPermission)
        add(accompanistSystemUiController)
        add(accompanistAnimation)
        add(accompanistFlowLayout)
        add(crashX)
        add(Hilt)
        add(retrofit)
        add(retrofitGsonConverter)
        add(okhttpInterceptor)
    }
    val GatewayLibraries = arrayListOf<String>().apply {
        add(KotlinXCoroutine)
        add(ktorCore)
        add(ktorClientCIO)
        add(ktorWebSockets)
        add(ktorGson)
    }

}

//util functions for adding the different type dependencies from build.gradle file
fun DependencyHandler.kapt(list: List<String>) {
    list.forEach { dependency ->
        add("kapt", dependency)
    }
}

fun DependencyHandler.implementation(list: List<String>) {
    list.forEach { dependency ->
        add("implementation", dependency)
    }
}