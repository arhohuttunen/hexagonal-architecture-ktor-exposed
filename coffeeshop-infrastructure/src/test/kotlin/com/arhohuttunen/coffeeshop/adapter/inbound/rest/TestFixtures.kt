package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.application.ports.outbound.Payments
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.Payment

class TestFixtures(private val orders: Orders, private val payments: Payments) {
    operator fun invoke(order: Order) = orders.save(order)
    operator fun invoke(payment: Payment) = payments.save(payment)
}
