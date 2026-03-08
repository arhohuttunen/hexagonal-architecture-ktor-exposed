package com.arhohuttunen.coffeeshop.application.ports.inbound

import arrow.core.Either
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.OrderError
import kotlin.uuid.Uuid

interface PreparingCoffee {
    fun startPreparingOrder(orderId: Uuid): Either<OrderError, Order>
    fun finishPreparingOrder(orderId: Uuid): Either<OrderError, Order>
}
