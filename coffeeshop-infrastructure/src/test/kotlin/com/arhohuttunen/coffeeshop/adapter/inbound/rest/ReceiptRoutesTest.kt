package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryOrders
import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryPayments
import com.arhohuttunen.coffeeshop.application.CoffeeShop
import com.arhohuttunen.coffeeshop.domain.Size
import com.arhohuttunen.coffeeshop.domain.aLineItem
import com.arhohuttunen.coffeeshop.domain.anOrder
import com.arhohuttunen.coffeeshop.domain.aPaymentForOrder
import com.arhohuttunen.coffeeshop.domain.aReadyOrder
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
        withReceiptRoutes { given ->
            val order = given(aReadyOrder())
            given(aPaymentForOrder(order.id))

            val response = get("/receipts/${order.id}")

            response shouldHaveStatus HttpStatusCode.OK
        }
    }

    test("returns receipt details when reading a receipt") {
        withReceiptRoutes { given ->
            val order = given(aReadyOrder { items(aLineItem { size = Size.LARGE }) })
            given(aPaymentForOrder(order.id))

            val response = get("/receipts/${order.id}")

            val body = response.bodyAsText()

            body.shouldContainJsonKeyValue("$.amount", "5.00")
            body.shouldContainJsonKey("$.paidAt")
        }
    }

    test("returns ok when taking an order") {
        withReceiptRoutes { given ->
            val order = given(aReadyOrder())

            val response = delete("/receipts/${order.id}")

            response shouldHaveStatus HttpStatusCode.OK
        }
    }

    test("returns not found when reading a receipt for a non-existent order") {
        withReceiptRoutes { _ ->
            val response = get("/receipts/${Uuid.random()}")

            response shouldHaveStatus HttpStatusCode.NotFound
        }
    }

    test("returns conflict when taking an order that is not ready") {
        withReceiptRoutes { given ->
            val order = given(anOrder())

            val response = delete("/receipts/${order.id}")

            response shouldHaveStatus HttpStatusCode.Conflict
        }
    }
})

fun withReceiptRoutes(test: suspend HttpClient.(TestFixtures) -> Unit) {
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
        }.use { client -> test(client, TestFixtures(orders, payments)) }
    }
}
