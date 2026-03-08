package com.arhohuttunen.coffeeshop

import io.ktor.server.application.*
import io.ktor.server.config.*
import org.jetbrains.exposed.v1.jdbc.Database

fun Application.configureDatabase(config: ApplicationConfig) {
    Database.connect(
        url = config.property("database.url").getString(),
        driver = config.property("database.driver").getString(),
        user = config.property("database.user").getString(),
        password = config.property("database.password").getString(),
    )
}
