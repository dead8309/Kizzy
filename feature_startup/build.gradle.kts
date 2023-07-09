plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
    id ("kizzy.android.feature")
}

android {
    namespace = "com.my.kizzy.feature_startup"
}

dependencies {
    implementation (libs.activity.compose)
    implementation (libs.material.icons.extended)
    implementation (libs.accompanist.permission)
    implementation (libs.kotlinx.serialization.json)
}