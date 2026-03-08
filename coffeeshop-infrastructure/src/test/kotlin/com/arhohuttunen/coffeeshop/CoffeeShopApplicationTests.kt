package com.arhohuttunen.coffeeshop

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerProjectExtension
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.uuid.Uuid

class CoffeeShopApplicationTests : FunSpec({
    val postgres = install(TestContainerProjectExtension(PostgreSQLContainer<Nothing>("postgres")))

    test("process new order") {
        withTestApplication(postgres) {
            val orderId = placeOrder()
            payOrder(orderId)
            prepareOrder(orderId)
            finishPreparingOrder(orderId)
            readReceipt(orderId)
            takeOrder(orderId)
        }
    }

    test("update order before payment") {
        withTestApplication(postgres) {
            val orderId = placeOrder()
            updateOrder(orderId)
        }
    }

    test("cancel order before payment") {
        withTestApplication(postgres) {
            val orderId = placeOrder()
            cancelOrder(orderId)
        }
    }
})

fun withTestApplication(postgres: PostgreSQLContainer<Nothing>, test: suspend ApplicationTestBuilder.() -> Unit) {
    testApplication {
        environment {
            config = MapApplicationConfig(
                "database.url" to postgres.jdbcUrl,
                "database.driver" to postgres.driverClassName,
                "database.user" to postgres.username,
                "database.password" to postgres.password,
            )
        }
        application { module() }

        test()
    }
}

private val orderJson = """
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


suspend fun ApplicationTestBuilder.placeOrder(): Uuid {
    val result = client.post("/orders") {
        contentType(ContentType.Application.Json)
        setBody(orderJson)
    }

    result.status shouldBe HttpStatusCode.Created

    val location = result.headers[HttpHeaders.Location]!!
    return Uuid.parse(location.substring(location.lastIndexOf("/") + 1))
}

suspend fun ApplicationTestBuilder.updateOrder(orderId: Uuid) {
    val result = client.put("/orders/$orderId") {
        contentType(ContentType.Application.Json)
        setBody(orderJson)
    }
    result.status shouldBe HttpStatusCode.OK
}

suspend fun ApplicationTestBuilder.cancelOrder(orderId: Uuid) {
    val result = client.delete("/orders/$orderId")
    result.status shouldBe HttpStatusCode.NoContent
}

suspend fun ApplicationTestBuilder.payOrder(orderId: Uuid) {
    val result = client.put("/payments/$orderId") {
        contentType(ContentType.Application.Json)
        setBody(
            """
            {
                "cardHolderName": "Michael Faraday",
                "cardNumber": "11223344",
                "expiry": "2023-12"
            }
        """.trimIndent()
        )
    }
    result.status shouldBe HttpStatusCode.OK
}

suspend fun ApplicationTestBuilder.prepareOrder(orderId: Uuid) {
    val result = client.put("/orders/$orderId/preparation")
    result.status shouldBe HttpStatusCode.OK
}

suspend fun ApplicationTestBuilder.finishPreparingOrder(orderId: Uuid) {
    val result = client.delete("/orders/$orderId/preparation")
    result.status shouldBe HttpStatusCode.OK
}

suspend fun ApplicationTestBuilder.readReceipt(orderId: Uuid) {
    val result = client.get("/receipts/$orderId")
    result.status shouldBe HttpStatusCode.OK
}

suspend fun ApplicationTestBuilder.takeOrder(orderId: Uuid) {
    val result = client.delete("/receipts/$orderId")
    result.status shouldBe HttpStatusCode.OK
}
