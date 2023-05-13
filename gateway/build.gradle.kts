plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies{
    implementation (projects.domain)
    implementation (libs.kotlinx.coroutine)
    implementation (libs.bundles.network.ktor)
}