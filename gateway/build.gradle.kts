plugins {
    id("java-library")
    kotlin("jvm")
}
repositories {
    mavenCentral()
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
dependencies{
    implementation (AppDependencies.GatewayLibraries)
}