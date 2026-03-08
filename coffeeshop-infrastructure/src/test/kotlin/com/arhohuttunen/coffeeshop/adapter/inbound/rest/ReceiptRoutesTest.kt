package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryOrders
import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryPayments
import com.arhohuttunen.coffeeshop.application.CoffeeShop
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.application.ports.outbound.Payments
import com.arhohuttunen.coffeeshop.domain.Drink
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Milk
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.anOrder
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.aReadyOrder
import com.arhohuttunen.coffeeshop.domain.PaymentTestFactory.aPaymentForOrder
import com.arhohuttunen.coffeeshop.domain.Size
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.ktor.client.statement.bodyAsText
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

class ReceiptRoutesTest : FunSpec({
    test("returns ok when reading a receipt") {
        withReceiptRoutes { orders, payments ->
            val order = orders.save(aReadyOrder())
            payments.save(aPaymentForOrder(order.id))

            val response = get("/receipts/${order.id}")

            response shouldHaveStatus HttpStatusCode.OK
        }
    }

    test("returns receipt details when reading a receipt") {
        withReceiptRoutes { orders, payments ->
            val order = orders.save(
                Order.Ready(
                    id = Uuid.random(),
                    location = Location.TAKE_AWAY,
                    items = listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 1))
                )
            )
            payments.save(aPaymentForOrder(order.id))

            val body = get("/receipts/${order.id}").bodyAsText()

            body.shouldContainJsonKeyValue("$.amount", "5.00")
            body.shouldContainJsonKey("$.paidAt")
        }
    }

    test("returns ok when taking an order") {
        withReceiptRoutes { orders, _ ->
            val order = orders.save(aReadyOrder())

            val response = delete("/receipts/${order.id}")

            response shouldHaveStatus HttpStatusCode.OK
        }
    }

    test("returns not found when reading a receipt for a non-existent order") {
        withReceiptRoutes { _, _ ->
            val response = get("/receipts/${Uuid.random()}")

            response shouldHaveStatus HttpStatusCode.NotFound
        }
    }

    test("returns conflict when taking an order that is not ready") {
        withReceiptRoutes { orders, _ ->
            val order = orders.save(anOrder())

            val response = delete("/receipts/${order.id}")

            response shouldHaveStatus HttpStatusCode.Conflict
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
        install(Resources)
        routing {
            receiptRoutes(orderingCoffee)
        }
        createClient {
            install(ClientContentNegotiation)
        }.use { client -> test(client, orders, payments) }
    }
}
