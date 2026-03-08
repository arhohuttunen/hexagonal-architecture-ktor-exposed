plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    implementation(libs.arrowCore)
    implementation(libs.bundles.kotlinxEcosystem)
    testFixturesImplementation(libs.arrowCore)
    testFixturesImplementation(libs.bundles.kotlinxEcosystem)
    testImplementation(libs.kotestAssertionsArrow)
    testImplementation(libs.kotestRunnerJUnit5)
}