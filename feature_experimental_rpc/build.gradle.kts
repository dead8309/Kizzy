plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
    id ("kizzy.android.hilt")
    id ("kizzy.android.feature")
}

android {
    namespace = "com.my.kizzy.feature_experimental_rpc"
}

dependencies {
    implementation(libs.material.icons.extended)
    implementation(libs.activity.compose)
    implementation(libs.blankj.utilcodex)
    implementation(libs.coil)
    implementation(projects.featureRpcBase)
}
