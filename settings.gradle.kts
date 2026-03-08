dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":coffeeshop-infrastructure")
include(":coffeeshop-application")

rootProject.name = "hexagonal-architecture-ktor-exposed"