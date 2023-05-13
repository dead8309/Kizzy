plugins {
    id("kizzy.android.library")
    id("kizzy.android.hilt")
}

android {
    namespace = "com.my.kizzy.data"
}

dependencies {
    implementation (libs.core.ktx)
    implementation (projects.domain)
    implementation (libs.bundles.network.okhttp)
    implementation (libs.bundles.network.retrofit)
    implementation (libs.gson)
    implementation (libs.javax)
    implementation (projects.common.preference)
    implementation (projects.gateway)
    implementation (libs.blankj.utilcodex)
}