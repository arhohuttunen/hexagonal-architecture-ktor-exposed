package com.arhohuttunen.coffeeshop.application

import arrow.core.Either
import arrow.core.raise.either
import com.arhohuttunen.coffeeshop.application.ports.inbound.PreparingCoffee
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.application.ports.outbound.TransactionScope
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.OrderError
import kotlin.uuid.Uuid

class CoffeeMachine(
    private val orders: Orders,
    private val transactionScope: TransactionScope = TransactionScope()
) : PreparingCoffee {
    override fun startPreparingOrder(orderId: Uuid): Either<OrderError, Order> =
        transactionScope.execute {
            either {
                val order = orders.findById(orderId).bind()
                order.markBeingPrepared().bind().also { orders.save(it) }
            }
        }

    override fun finishPreparingOrder(orderId: Uuid): Either<OrderError, Order> =
        transactionScope.execute {
            either {
                val order = orders.findById(orderId).bind()
                order.markPrepared().bind().also { orders.save(it) }
            }
        }
}
