package com.arhohuttunen.coffeeshop.application

import com.arhohuttunen.coffeeshop.application.ports.inbound.PreparingCoffee
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.domain.Order
import kotlin.uuid.Uuid

class CoffeeMachine(private val orders: Orders) : PreparingCoffee {
    override fun startPreparingOrder(orderId: Uuid): Order {
        val order = orders.findById(orderId)

        return orders.save(order.markBeingPrepared())
    }
}