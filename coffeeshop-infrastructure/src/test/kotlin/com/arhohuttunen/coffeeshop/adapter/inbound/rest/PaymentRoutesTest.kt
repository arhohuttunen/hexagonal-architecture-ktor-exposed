package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryOrders
import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryPayments
import com.arhohuttunen.coffeeshop.application.CoffeeShop
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.anOrder
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication

class PaymentRoutesTest : FunSpec({
    val paymentJson = """
        {
            "cardHolderName": "Michael Faraday",
            "cardNumber": "11223344",
            "expiry": "2023-12"
        }
    """.trimIndent()
    val orders = InMemoryOrders()
    val payments = InMemoryPayments()
    val orderingCoffee = CoffeeShop(orders, payments)

    fun withPaymentRoutes(block: suspend HttpClient.() -> Unit) {
        testApplication {
            install(ContentNegotiation) {
                json()
            }
            routing {
                paymentRoutes(orderingCoffee)
            }
            createClient {
                install(ClientContentNegotiation)
            }.use { client -> block(client) }
        }
    }

    test("pay an order") {
        withPaymentRoutes {
            val order = orders.save(anOrder())

            val response = put("/payments/${order.id}") {
                contentType(ContentType.Application.Json)
                setBody(paymentJson)
            }

            response shouldHaveStatus HttpStatusCode.OK
        }
    }
})