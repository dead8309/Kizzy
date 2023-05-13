plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
}

android {
    namespace = "com.my.kizzy.navigation"
}

dependencies {
    implementation (libs.compose.ui)
    implementation (libs.compose.navigation)
    implementation (libs.accompanist.navigation.animation)
}