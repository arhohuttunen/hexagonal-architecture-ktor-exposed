package com.arhohuttunen.coffeeshop.application.ports.outbound

import com.arhohuttunen.coffeeshop.domain.Payment
import kotlin.uuid.Uuid

interface Payments {
    fun save(payment: Payment): Payment
    fun findByOrderId(orderId: Uuid): Payment
}