plugins {
    id("kizzy.android.library")
    id("kizzy.android.hilt")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.my.kizzy.data"
    defaultConfig {
        buildConfigFieldFromGradleProperty("BASE_URL","BASE_URL")
    }
}

dependencies {
    implementation (libs.core.ktx)
    implementation (projects.domain)
    implementation (libs.bundles.network.ktor.request)
    implementation (libs.javax)
    implementation (projects.common.preference)
    implementation (projects.gateway)
    implementation (libs.blankj.utilcodex)
    testImplementation(libs.junit)
}

fun com.android.build.api.dsl.LibraryDefaultConfig.buildConfigFieldFromGradleProperty(fieldName: String, gradlePropertyName: String) {
    val propertyValue = project.properties[gradlePropertyName] as? String
    if (propertyValue != null) {
        buildConfigField("String", fieldName, propertyValue)
    }
}