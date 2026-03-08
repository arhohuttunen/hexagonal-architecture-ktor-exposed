package com.arhohuttunen.coffeeshop.adapters.outbound

import com.arhohuttunen.coffeeshop.application.ports.outbound.Payments
import com.arhohuttunen.coffeeshop.domain.Payment
import kotlin.uuid.Uuid

class InMemoryPayments : Payments {
    private val payments = mutableMapOf<Uuid, Payment>()

    override fun save(payment: Payment): Payment {
        payments[payment.orderId] = payment
        return payment
    }
}