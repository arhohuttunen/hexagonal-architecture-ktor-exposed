package com.arhohuttunen.coffeeshop.application.ports.outbound

import com.arhohuttunen.coffeeshop.domain.Order

interface Orders {
    fun save(order: Order): Order
}