package com.arhohuttunen.coffeeshop.application.ports.inbound

import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Order

interface OrderingCoffee {
    fun placeOrder(location: Location, items: List<LineItem>): Order
}