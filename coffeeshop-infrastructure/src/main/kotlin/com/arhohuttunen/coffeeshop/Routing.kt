package com.arhohuttunen.coffeeshop

import com.arhohuttunen.coffeeshop.adapter.inbound.rest.orderRoutes
import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryOrders
import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryPayments
import com.arhohuttunen.coffeeshop.application.CoffeeShop
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        orderRoutes(CoffeeShop(InMemoryOrders(), InMemoryPayments()))
    }
}