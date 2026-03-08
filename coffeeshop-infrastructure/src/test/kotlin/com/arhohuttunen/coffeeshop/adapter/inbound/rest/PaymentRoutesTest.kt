package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryOrders
import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryPayments
import com.arhohuttunen.coffeeshop.application.CoffeeShop
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.anOrder
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.Resources
import io.ktor.server.testing.*
import kotlin.uuid.Uuid
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

class PaymentRoutesTest : FunSpec({
    val paymentJson = """
        {
            "cardHolderName": "Michael Faraday",
            "cardNumber": "11223344",
            "expiry": "2023-12"
        }
    """.trimIndent()

    test("returns ok when paying an order") {
        withPaymentRoutes { orders ->
            val order = orders.save(anOrder())

            val response = put("/payments/${order.id}") {
                contentType(ContentType.Application.Json)
                setBody(paymentJson)
            }

            response shouldHaveStatus HttpStatusCode.OK
        }
    }

    test("returns not found when order does not exist") {
        withPaymentRoutes {
            val response = put("/payments/${Uuid.random()}") {
                contentType(ContentType.Application.Json)
                setBody(paymentJson)
            }

            response shouldHaveStatus HttpStatusCode.NotFound
        }
    }
})

fun withPaymentRoutes(test: suspend HttpClient.(orders: Orders) -> Unit) {
    val orders = InMemoryOrders()
    val payments = InMemoryPayments()
    val orderingCoffee = CoffeeShop(orders, payments)

    testApplication {
        install(ContentNegotiation) {
            json()
        }
        install(Resources)
        routing {
            paymentRoutes(orderingCoffee)
        }
        createClient {
            install(ClientContentNegotiation)
        }.use { client -> test(client, orders) }
    }
}
