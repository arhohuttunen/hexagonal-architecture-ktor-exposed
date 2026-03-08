package com.arhohuttunen.coffeeshop.application.ports.outbound

import com.arhohuttunen.coffeeshop.domain.Order
import kotlin.uuid.Uuid

interface Orders {
    fun save(order: Order): Order
    fun findById(orderId: Uuid): Order
    fun deleteById(orderId: Uuid)
}