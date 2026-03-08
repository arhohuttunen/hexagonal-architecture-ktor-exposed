package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.application.ports.inbound.OrderingCoffee
import com.arhohuttunen.coffeeshop.domain.CreditCard
import com.arhohuttunen.coffeeshop.domain.Payment
import io.ktor.http.*
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.YearMonth
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

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
    put("/payments/{id}") {
        val request = call.receive<PaymentRequest>()
        val payment = orderingCoffee.payOrder(Uuid.parse(call.parameters["id"]!!), request.creditCard())
        call.respond(HttpStatusCode.OK, PaymentResponse.fromDomain(payment))
    }
}
