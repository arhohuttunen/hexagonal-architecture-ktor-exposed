package com.arhohuttunen.coffeeshop

import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val dependencies = Dependencies()
    configureDatabase(environment.config)
    configureRouting(dependencies)
}