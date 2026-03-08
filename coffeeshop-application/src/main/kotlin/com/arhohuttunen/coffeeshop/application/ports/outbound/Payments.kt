package com.arhohuttunen.coffeeshop.application.ports.outbound

import arrow.core.Either
import com.arhohuttunen.coffeeshop.domain.OrderError
import com.arhohuttunen.coffeeshop.domain.Payment
import kotlin.uuid.Uuid

interface Payments {
    fun save(payment: Payment): Payment
    fun findByOrderId(orderId: Uuid): Either<OrderError, Payment>
}
