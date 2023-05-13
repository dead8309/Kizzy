plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
    id ("kizzy.android.feature")
}

android {
    namespace = "com.my.kizzy.feature_media_rpc"
}

dependencies {
    implementation(projects.featureRpcBase)
    implementation(libs.material.icons.extended)
}