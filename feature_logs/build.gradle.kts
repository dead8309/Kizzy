plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
    id ("kizzy.android.feature")
}

android {
    namespace = "com.my.kizzy.feature_logs"
}

dependencies {
    implementation (projects.theme)
    implementation(libs.activity.compose)
}