package com.arhohuttunen.coffeeshop

import com.arhohuttunen.coffeeshop.adapter.inbound.rest.orderRoutes
import com.arhohuttunen.coffeeshop.adapter.inbound.rest.paymentRoutes
import com.arhohuttunen.coffeeshop.adapter.inbound.rest.receiptRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.Resources
import io.ktor.server.routing.*

fun Application.configureRouting(dependencies: Dependencies) {
    install(ContentNegotiation) {
        json()
    }
    install(Resources)
    routing {
        orderRoutes(dependencies.orderingCoffee, dependencies.preparingCoffee)
        paymentRoutes(dependencies.orderingCoffee)
        receiptRoutes(dependencies.orderingCoffee)
    }
}
