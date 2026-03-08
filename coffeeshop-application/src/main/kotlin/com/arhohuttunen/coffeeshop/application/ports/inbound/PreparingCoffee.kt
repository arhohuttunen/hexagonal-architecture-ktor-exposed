package com.arhohuttunen.coffeeshop.application.ports.inbound

import com.arhohuttunen.coffeeshop.domain.Order
import kotlin.uuid.Uuid

interface PreparingCoffee {
    fun startPreparingOrder(orderId: Uuid): Order
    fun finishPreparingOrder(orderId: Uuid): Order
}