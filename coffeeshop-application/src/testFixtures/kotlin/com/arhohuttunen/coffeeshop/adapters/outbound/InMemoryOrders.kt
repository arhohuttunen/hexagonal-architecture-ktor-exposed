package com.arhohuttunen.coffeeshop.adapters.outbound

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.OrderError
import kotlin.uuid.Uuid

class InMemoryOrders : Orders {
    private val orders = mutableMapOf<Uuid, Order>()

    override fun save(order: Order): Order {
        orders[order.id] = order
        return order
    }

    override fun findById(orderId: Uuid): Either<OrderError, Order> =
        orders[orderId]?.right() ?: OrderError.NotFound.left()

    override fun deleteById(orderId: Uuid) {
        orders.remove(orderId)
    }
}
