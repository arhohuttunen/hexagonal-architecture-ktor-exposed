package com.arhohuttunen.coffeeshop.application

import com.arhohuttunen.coffeeshop.application.ports.inbound.PreparingCoffee
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.application.ports.outbound.TransactionScope
import com.arhohuttunen.coffeeshop.domain.Order
import kotlin.uuid.Uuid

class CoffeeMachine(
    private val orders: Orders,
    private val transactionScope: TransactionScope = TransactionScope()
) : PreparingCoffee {
    override fun startPreparingOrder(orderId: Uuid): Order =
        transactionScope.execute {
            val order = orders.findById(orderId)
            orders.save(order.markBeingPrepared())
        }

    override fun finishPreparingOrder(orderId: Uuid): Order =
        transactionScope.execute {
            val order = orders.findById(orderId)
            orders.save(order.markPrepared())
        }
}
