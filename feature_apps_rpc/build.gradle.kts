plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
    id ("kizzy.android.feature")
}

android {
    namespace = "com.my.kizzy.feature_apps_rpc"
}

dependencies {
    implementation (libs.blankj.utilcodex)
    implementation (libs.glide)
    implementation(projects.featureRpcBase)
    implementation (libs.material.icons.extended)
}