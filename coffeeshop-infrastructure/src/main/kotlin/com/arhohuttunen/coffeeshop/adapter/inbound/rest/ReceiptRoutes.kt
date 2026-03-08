package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.application.ports.inbound.OrderingCoffee
import com.arhohuttunen.coffeeshop.domain.Receipt
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
@Resource("/receipts")
class Receipts {
    @Serializable
    @Resource("{id}")
    data class ById(val parent: Receipts = Receipts(), val id: Uuid)
}

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
    get<Receipts.ById> { resource ->
        orderingCoffee.readReceipt(resource.id)
            .fold(
                { error -> call.respondError(error) },
                { receipt -> call.respond(HttpStatusCode.OK, ReceiptResponse.fromDomain(receipt)) }
            )
    }
    delete<Receipts.ById> { resource ->
        orderingCoffee.takeOrder(resource.id)
            .fold(
                { error -> call.respondError(error) },
                { order -> call.respond(HttpStatusCode.OK, OrderResponse.fromDomain(order)) }
            )
    }
}
