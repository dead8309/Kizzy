plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
    id ("kizzy.android.feature")
    id ("kizzy.android.hilt")
}

android {
    namespace = "com.my.kizzy.feature_custom_rpc"
}

dependencies {
    implementation (libs.material.icons.extended)
    implementation(libs.accompanist.permission)
    implementation(libs.activity.compose)
    implementation(libs.blankj.utilcodex)
    implementation(libs.glide)
    implementation(libs.kotlinx.serialization.json)
    implementation(projects.featureRpcBase)
    implementation(projects.featureProfile)
}