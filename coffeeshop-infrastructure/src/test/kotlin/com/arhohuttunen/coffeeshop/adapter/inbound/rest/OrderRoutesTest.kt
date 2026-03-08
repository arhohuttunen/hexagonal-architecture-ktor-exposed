package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryOrders
import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryPayments
import com.arhohuttunen.coffeeshop.application.CoffeeMachine
import com.arhohuttunen.coffeeshop.application.CoffeeShop
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.aPaidOrder
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.anOrderInPreparation
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.anOrder
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

class OrderRoutesTest : FunSpec({
    val orderJson = """
        {
            "location": "IN_STORE",
            "items": [{
                "drink": "LATTE",
                "quantity": 1,
                "milk": "WHOLE",
                "size": "LARGE"
            }]
        }
    """.trimIndent()

    test("create an order") {
        withOrderRoutes {
            val response = post("/orders") {
                contentType(ContentType.Application.Json)
                setBody(orderJson)
            }

            response shouldHaveStatus HttpStatusCode.Created
        }
    }

    test("update an order") {
        withOrderRoutes { orders ->
            val order = orders.save(anOrder())

            val response = put("/orders/${order.id}") {
                contentType(ContentType.Application.Json)
                setBody(orderJson)
            }

            response shouldHaveStatus HttpStatusCode.OK
        }
    }

    test("cancel an order") {
        withOrderRoutes { orders ->
            val order = orders.save(anOrder())

            val response = delete("/orders/${order.id}")

            response shouldHaveStatus HttpStatusCode.NoContent
        }
    }

    test("prepare an order") {
        withOrderRoutes { orders ->
            val order = orders.save(aPaidOrder())

            val response = put("/orders/${order.id}/preparation")

            response shouldHaveStatus HttpStatusCode.OK
        }
    }

    test("finish preparing an order") {
        withOrderRoutes { orders ->
            val order = orders.save(anOrderInPreparation())

            val response = delete("/orders/${order.id}/preparation")

            response shouldHaveStatus HttpStatusCode.OK
        }
    }
})

fun withOrderRoutes(test: suspend HttpClient.(orders: Orders) -> Unit) {
    val orders = InMemoryOrders()
    val payments = InMemoryPayments()
    val orderingCoffee = CoffeeShop(orders, payments)
    val preparingCoffee = CoffeeMachine(orders)

    testApplication {
        install(ContentNegotiation) {
            json()
        }
        routing {
            orderRoutes(orderingCoffee, preparingCoffee)
        }
        createClient {
            install(ClientContentNegotiation)
        }.use { client -> test(client, orders) }
    }
}
