plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies{
    implementation (libs.kotlinx.coroutine)
    implementation (libs.bundles.network.ktor)
}