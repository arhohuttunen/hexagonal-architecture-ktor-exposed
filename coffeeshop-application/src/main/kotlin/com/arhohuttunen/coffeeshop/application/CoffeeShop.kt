package com.arhohuttunen.coffeeshop.application

import com.arhohuttunen.coffeeshop.application.ports.inbound.OrderingCoffee
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.application.ports.outbound.Payments
import com.arhohuttunen.coffeeshop.domain.CreditCard
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.Payment
import com.arhohuttunen.coffeeshop.domain.Receipt
import kotlin.time.Clock
import kotlin.uuid.Uuid

class CoffeeShop(private val orders: Orders, private val payments: Payments) : OrderingCoffee {
    override fun placeOrder(
        location: Location,
        items: List<LineItem>
    ): Order {
        return orders.save(Order(location = location, items = items))
    }

    override fun updateOrder(
        orderId: Uuid,
        location: Location,
        items: List<LineItem>
    ): Order {
        val existingOrder = orders.findById(orderId)

        return orders.save(existingOrder.update(location, items))
    }

    override fun cancelOrder(orderId: Uuid) {
        val order = orders.findById(orderId)

        if (!order.canBeCancelled()) throw IllegalStateException("Order is already paid")

        orders.deleteById(orderId)
    }

    override fun payOrder(
        orderId: Uuid,
        creditCard: CreditCard
    ): Payment {
        val order = orders.findById(orderId)

        orders.save(order.markPaid())

        return payments.save(Payment(orderId, creditCard, Clock.System.now()))
    }

    override fun readReceipt(orderId: Uuid): Receipt {
        val order = orders.findById(orderId)
        val payment = payments.findByOrderId(orderId)

        return Receipt(order.cost(), payment.paidAt)
    }

    override fun takeOrder(orderId: Uuid): Order {
        val order = orders.findById(orderId)

        return orders.save(order.markTaken())
    }
}