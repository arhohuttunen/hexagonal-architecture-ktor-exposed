plugins {
    id("buildsrc.convention.kotlin-jvm")

    application
}

dependencies {
    implementation(project(":coffeeshop-application"))
}

application {
    mainClass = "com.arhohuttunen.coffeeshop.ApplicationKt"
}
