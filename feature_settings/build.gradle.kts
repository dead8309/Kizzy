plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
    id ("kizzy.android.feature")
}

android {
    namespace = "com.my.kizzy.feature_settings"

    defaultConfig {
        buildConfigField("String","VERSION_NAME", "\"${libs.versions.version.name.get()}\"")
    }
}

dependencies {
    implementation(libs.androidx.material)
    implementation(libs.material.icons.extended)
    implementation(libs.glide)
    implementation(libs.accompanist.pager.layouts)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.coil)
    implementation(libs.coil.svg)
    implementation(libs.android.svg)
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.color)
    implementation(projects.theme)
}