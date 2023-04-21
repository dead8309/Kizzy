plugins {
    alias(libs.plugins.kotlin.jvm)
}
kotlin {
    jvmToolchain(17)
}

dependencies{
    implementation (libs.kotlinx.coroutine)
    implementation (libs.bundles.network.ktor)
}