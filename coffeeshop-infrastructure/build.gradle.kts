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
    implementation(libs.ktorSerializationKotlinxJson)
    implementation(libs.ktorServerConfigYaml)
    implementation(libs.ktorServerContentNegotiation)
    implementation(libs.ktorServerCore)
    implementation(libs.ktorServerNetty)
    implementation(libs.logback)
    testImplementation(libs.kotestAssertionsKtor)
    testImplementation(libs.kotestRunnerJUnit5)
    testImplementation(libs.ktorClientContentNegotiation)
    testImplementation(libs.ktorClientCore)
    testImplementation(libs.ktorServerTestHost)
}
