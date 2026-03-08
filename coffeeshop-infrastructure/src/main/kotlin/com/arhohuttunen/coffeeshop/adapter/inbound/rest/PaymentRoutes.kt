package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.application.ports.inbound.OrderingCoffee
import com.arhohuttunen.coffeeshop.domain.CreditCard
import com.arhohuttunen.coffeeshop.domain.Payment
import io.ktor.http.*
import io.ktor.resources.Resource
import io.ktor.server.request.receive
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.YearMonth
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
@Resource("/payments")
class Payments {
    @Serializable
    @Resource("{id}")
    data class ById(val parent: Payments = Payments(), val id: Uuid)
}

@Serializable
data class PaymentRequest(
    val cardHolderName: String,
    val cardNumber: String,
    val expiry: YearMonth
) {
    fun creditCard() =
        CreditCard(
            cardHolderName,
            cardNumber,
            expiry
        )
}

@Serializable
data class PaymentResponse(
    val cardHolderName: String,
    val cardNumber: String,
    val expiry: YearMonth,
    val paidAt: Instant
) {
    companion object {
        fun fromDomain(payment: Payment) =
            PaymentResponse(
                payment.creditCard.cardHolderName,
                payment.creditCard.cardNumber,
                payment.creditCard.expiry,
                payment.paidAt
            )
    }
}

fun Route.paymentRoutes(orderingCoffee: OrderingCoffee) {
    put<Payments.ById> { resource ->
        val request = call.receive<PaymentRequest>()
        orderingCoffee.payOrder(resource.id, request.creditCard())
            .fold(
                { call.respondError(it) },
                { call.respond(HttpStatusCode.OK, PaymentResponse.fromDomain(it)) }
            )
    }
}
