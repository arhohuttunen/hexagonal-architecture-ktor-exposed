package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryOrders
import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryPayments
import com.arhohuttunen.coffeeshop.application.CoffeeShop
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.aReadyOrder
import com.arhohuttunen.coffeeshop.domain.PaymentTestFactory.aPaymentForOrder
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication

class ReceiptRoutesTest : FunSpec({
    val orders = InMemoryOrders()
    val payments = InMemoryPayments()
    val orderingCoffee = CoffeeShop(orders, payments)

    fun withReceiptRoutes(block: suspend HttpClient.() -> Unit) {
        testApplication {
            install(ContentNegotiation) {
                json()
            }
            routing {
                receiptRoutes(orderingCoffee)
            }
            createClient {
                install(ClientContentNegotiation)
            }.use { client -> block(client) }
        }
    }

    test("read a receipt") {
        withReceiptRoutes {
            val order = orders.save(aReadyOrder())
            payments.save(aPaymentForOrder(order.id))

            val response = get("/receipt/${order.id}")

            response shouldHaveStatus HttpStatusCode.OK
        }
    }

    test("take an order") {
        withReceiptRoutes {
            val order = orders.save(aReadyOrder())

            val response = delete("/receipt/${order.id}")

            response shouldHaveStatus HttpStatusCode.OK
        }
    }
})