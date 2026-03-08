package com.arhohuttunen.coffeeshop.application.ports.outbound

import arrow.core.Either
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.OrderError
import kotlin.uuid.Uuid

interface Orders {
    fun save(order: Order): Order
    fun findById(orderId: Uuid): Either<OrderError, Order>
    fun deleteById(orderId: Uuid)
}
