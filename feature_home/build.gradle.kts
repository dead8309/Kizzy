plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
    id ("kizzy.android.feature")
}

android {
    namespace = "com.my.kizzy.feature_home"
}

dependencies {
    implementation (libs.accompanist.flowLayout)
    implementation (projects.featureRpcBase)
    implementation (projects.featureSettings)
    implementation (projects.common.navigation)
    implementation (libs.glide)
}