package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryOrders
import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryPayments
import com.arhohuttunen.coffeeshop.application.CoffeeShop
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.application.ports.outbound.Payments
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.aReadyOrder
import com.arhohuttunen.coffeeshop.domain.PaymentTestFactory.aPaymentForOrder
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

class ReceiptRoutesTest : FunSpec({
    test("read a receipt") {
        withReceiptRoutes { orders, payments ->
            val order = orders.save(aReadyOrder())
            payments.save(aPaymentForOrder(order.id))

            val response = get("/receipt/${order.id}")

            response shouldHaveStatus HttpStatusCode.OK
        }
    }

    test("take an order") {
        withReceiptRoutes { orders, _ ->
            val order = orders.save(aReadyOrder())

            val response = delete("/receipt/${order.id}")

            response shouldHaveStatus HttpStatusCode.OK
        }
    }
})

fun withReceiptRoutes(test: suspend HttpClient.(orders: Orders, payments: Payments) -> Unit) {
    val orders = InMemoryOrders()
    val payments = InMemoryPayments()
    val orderingCoffee = CoffeeShop(orders, payments)

    testApplication {
        install(ContentNegotiation) {
            json()
        }
        routing {
            receiptRoutes(orderingCoffee)
        }
        createClient {
            install(ClientContentNegotiation)
        }.use { client -> test(client, orders, payments) }
    }
}
