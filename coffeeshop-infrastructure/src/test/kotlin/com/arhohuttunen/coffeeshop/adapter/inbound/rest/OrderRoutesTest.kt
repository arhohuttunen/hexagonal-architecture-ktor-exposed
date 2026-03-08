package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryOrders
import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryPayments
import com.arhohuttunen.coffeeshop.application.CoffeeMachine
import com.arhohuttunen.coffeeshop.application.CoffeeShop
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.domain.aPaidOrder
import com.arhohuttunen.coffeeshop.domain.anOrder
import com.arhohuttunen.coffeeshop.domain.anOrderInPreparation
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
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

class OrderRoutesTest : FunSpec({
    test("returns created when placing an order") {
        withOrderRoutes {
            val response = post("/orders") {
                contentType(ContentType.Application.Json)
                setBody(someOrderContents)
            }

            response shouldHaveStatus HttpStatusCode.Created
        }
    }

    test("returns order details when placing an order") {
        withOrderRoutes {
            val response = post("/orders") {
                contentType(ContentType.Application.Json)
                setBody("""
                    {
                        "location": "IN_STORE",
                        "items": [{"drink": "LATTE", "milk": "WHOLE", "size": "LARGE", "quantity": 1}]
                    }
                """.trimIndent())
            }

            val body = response.bodyAsText()

            body.shouldContainJsonKeyValue("$.location", "IN_STORE")
            body.shouldContainJsonKeyValue("$.items[0].drink", "LATTE")
            body.shouldContainJsonKeyValue("$.cost", "5.00")
        }
    }

    test("returns ok when updating an order") {
        withOrderRoutes { orders ->
            val order = orders.save(anOrder())

            val response = put("/orders/${order.id}") {
                contentType(ContentType.Application.Json)
                setBody(someOrderContents)
            }

            response shouldHaveStatus HttpStatusCode.OK
        }
    }

    test("returns no content when cancelling an order") {
        withOrderRoutes { orders ->
            val order = orders.save(anOrder())

            val response = delete("/orders/${order.id}")

            response shouldHaveStatus HttpStatusCode.NoContent
        }
    }

    test("returns ok when starting preparation of an order") {
        withOrderRoutes { orders ->
            val order = orders.save(aPaidOrder())

            val response = put("/orders/${order.id}/preparation")

            response shouldHaveStatus HttpStatusCode.OK
        }
    }

    test("returns ok when finishing preparation of an order") {
        withOrderRoutes { orders ->
            val order = orders.save(anOrderInPreparation())

            val response = delete("/orders/${order.id}/preparation")

            response shouldHaveStatus HttpStatusCode.OK
        }
    }

    test("returns conflict when updating a paid order") {
        withOrderRoutes { orders ->
            val order = orders.save(aPaidOrder())

            val response = put("/orders/${order.id}") {
                contentType(ContentType.Application.Json)
                setBody(someOrderContents)
            }

            response shouldHaveStatus HttpStatusCode.Conflict
        }
    }

    test("returns conflict when cancelling a paid order") {
        withOrderRoutes { orders ->
            val order = orders.save(aPaidOrder())

            val response = delete("/orders/${order.id}")

            response shouldHaveStatus HttpStatusCode.Conflict
        }
    }

    test("returns conflict when preparing an unpaid order") {
        withOrderRoutes { orders ->
            val order = orders.save(anOrder())

            val response = put("/orders/${order.id}/preparation")

            response shouldHaveStatus HttpStatusCode.Conflict
        }
    }
})

private val someOrderContents = """
        {
            "location": "IN_STORE",
            "items": [{"drink": "LATTE", "quantity": 1, "milk": "WHOLE", "size": "LARGE"}]
        }
    """.trimIndent()


fun withOrderRoutes(test: suspend HttpClient.(orders: Orders) -> Unit) {
    val orders = InMemoryOrders()
    val payments = InMemoryPayments()
    val orderingCoffee = CoffeeShop(orders, payments)
    val preparingCoffee = CoffeeMachine(orders)

    testApplication {
        install(ContentNegotiation) {
            json()
        }
        install(Resources)
        routing {
            orderRoutes(orderingCoffee, preparingCoffee)
        }
        createClient {
            install(ClientContentNegotiation)
        }.use { client -> test(client, orders) }
    }
}
