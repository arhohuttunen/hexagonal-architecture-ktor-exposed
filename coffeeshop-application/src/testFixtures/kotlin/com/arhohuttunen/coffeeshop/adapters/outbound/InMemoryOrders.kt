package com.arhohuttunen.coffeeshop.adapters.outbound

import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.domain.Order
import kotlin.uuid.Uuid

class InMemoryOrders : Orders {
    private val orders = mutableMapOf<Uuid, Order>()

    override fun save(order: Order): Order {
        orders[order.id] = order
        return order
    }
}