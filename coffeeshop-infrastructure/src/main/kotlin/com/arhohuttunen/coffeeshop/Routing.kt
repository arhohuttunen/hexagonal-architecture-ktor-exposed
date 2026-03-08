package com.arhohuttunen.coffeeshop

import com.arhohuttunen.coffeeshop.adapter.inbound.rest.orderRoutes
import com.arhohuttunen.coffeeshop.adapter.inbound.rest.paymentRoutes
import com.arhohuttunen.coffeeshop.adapter.inbound.rest.receiptRoutes
import com.arhohuttunen.coffeeshop.application.ports.outbound.OrderNotFound
import com.arhohuttunen.coffeeshop.application.ports.outbound.PaymentNotFound
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Application.configureRouting(dependencies: Dependencies) {
    install(ContentNegotiation) {
        json()
    }
    routing {
        orderRoutes(dependencies.orderingCoffee, dependencies.preparingCoffee)
        paymentRoutes(dependencies.orderingCoffee)
        receiptRoutes(dependencies.orderingCoffee)
    }
    install(StatusPages) {
        exception<OrderNotFound> { call, e ->
            call.respond(HttpStatusCode.NotFound)
        }
        exception<PaymentNotFound> { call, e ->
            call.respond(HttpStatusCode.NotFound)
        }
        exception<IllegalStateException> { call, e ->
            call.respond(HttpStatusCode.Conflict)
        }
    }
}