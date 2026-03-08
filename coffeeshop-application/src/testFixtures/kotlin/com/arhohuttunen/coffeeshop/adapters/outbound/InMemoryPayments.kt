package com.arhohuttunen.coffeeshop.adapters.outbound

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.arhohuttunen.coffeeshop.application.ports.outbound.Payments
import com.arhohuttunen.coffeeshop.domain.OrderError
import com.arhohuttunen.coffeeshop.domain.Payment
import kotlin.uuid.Uuid

class InMemoryPayments : Payments {
    private val payments = mutableMapOf<Uuid, Payment>()

    override fun save(payment: Payment): Payment {
        payments[payment.orderId] = payment
        return payment
    }

    override fun findByOrderId(orderId: Uuid): Either<OrderError, Payment> =
        payments[orderId]?.right() ?: OrderError.PaymentNotFound.left()
}
