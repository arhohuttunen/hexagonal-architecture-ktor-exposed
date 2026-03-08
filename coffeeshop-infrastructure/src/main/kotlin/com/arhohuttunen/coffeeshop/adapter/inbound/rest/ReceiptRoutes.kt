package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.application.ports.inbound.OrderingCoffee
import com.arhohuttunen.coffeeshop.domain.Receipt
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class ReceiptResponse(
    @Serializable(with = BigDecimalSerializer::class)
    val amount: BigDecimal,
    val paidAt: Instant
) {
    companion object {
        fun fromDomain(receipt: Receipt) =
            ReceiptResponse(
                receipt.amount,
                receipt.paidAt
            )
    }
}

fun Route.receiptRoutes(orderingCoffee: OrderingCoffee) {
    get("/receipt/{id}") {
        val receipt = orderingCoffee.readReceipt(Uuid.parse(call.parameters["id"]!!))
        call.respond(HttpStatusCode.OK, ReceiptResponse.fromDomain(receipt))
    }
    delete("/receipt/{id}") {
        val order = orderingCoffee.takeOrder(Uuid.parse(call.parameters["id"]!!))
        call.respond(HttpStatusCode.OK, OrderResponse.fromDomain(order))
    }
}