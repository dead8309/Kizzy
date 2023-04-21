plugins {
    alias(libs.plugins.kotlin.jvm)
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies{
    implementation (libs.kotlinx.coroutine)
    implementation (libs.bundles.network.ktor)
}