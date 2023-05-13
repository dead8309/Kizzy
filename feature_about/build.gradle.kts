plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
    id ("kizzy.android.feature")
    id ("kizzy.android.hilt")
}

android {
    namespace = "com.my.kizzy.feature_about"

    defaultConfig {
        buildConfigField("String","VERSION_NAME", "\"${libs.versions.version.name.get()}\"")
    }
}

dependencies {
    implementation (libs.glide)
    implementation (libs.material.icons.extended)
    implementation (libs.activity.compose)
}
