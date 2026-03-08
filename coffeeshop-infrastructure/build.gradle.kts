plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.ktorPlugin)

    application
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(project(":coffeeshop-application"))
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.bundles.ktorServer)
    implementation(libs.logback)
    testImplementation(testFixtures(project(":coffeeshop-application")))
    testImplementation(libs.kotestAssertionsKtor)
    testImplementation(libs.kotestRunnerJUnit5)
    testImplementation(libs.ktorClientContentNegotiation)
    testImplementation(libs.ktorClientCore)
    testImplementation(libs.ktorServerTestHost)
}
