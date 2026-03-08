package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.module
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*

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
        testApplication {
            application { module() }
            val client = createClient {
                install(ContentNegotiation)
            }

            val response = client.post("/orders") {
                contentType(ContentType.Application.Json)
                setBody(orderJson)
            }

            response shouldHaveStatus HttpStatusCode.Created
        }
    }
})