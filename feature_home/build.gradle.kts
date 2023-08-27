plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
    id ("kizzy.android.feature")
}

android {
    namespace = "com.my.kizzy.feature_home"
    defaultConfig {
        buildConfigField("String","VERSION_NAME", "\"${libs.versions.version.name.get()}\"")
    }
}

dependencies {
    implementation (libs.accompanist.flowLayout)
    implementation (libs.material.icons.extended)
    implementation (projects.featureRpcBase)
    implementation (projects.featureSettings)
    implementation (projects.common.navigation)
    implementation (libs.glide)
    implementation (libs.activity.compose)
}