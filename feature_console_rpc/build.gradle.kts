plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
    id ("kizzy.android.feature")
    id ("kizzy.android.hilt")
}

android {
    namespace = "com.my.kizzy.feature_console_rpc"
}

dependencies {
    implementation (projects.featureRpcBase)
    implementation (libs.material.icons.extended)
    implementation (libs.glide)
    implementation (libs.activity.compose)
    implementation (libs.kotlinx.serialization.json)
}