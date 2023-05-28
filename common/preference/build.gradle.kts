plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
}

android {
    namespace = "com.my.kizzy.preference"
}

dependencies {
    implementation(projects.color)
    implementation(projects.domain)
    implementation(projects.common.resources)
    implementation(libs.material3)
    implementation(libs.mmkv)
    implementation(libs.kotlinx.coroutine)
    implementation(libs.compose.ui)
    implementation(libs.kotlinx.serialization.json)
}