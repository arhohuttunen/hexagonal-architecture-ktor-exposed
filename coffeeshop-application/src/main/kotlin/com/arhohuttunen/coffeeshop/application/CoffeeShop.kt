package com.arhohuttunen.coffeeshop.application

import com.arhohuttunen.coffeeshop.application.ports.inbound.OrderingCoffee
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Order

class CoffeeShop(private val orders: Orders) : OrderingCoffee {
    override fun placeOrder(
        location: Location,
        items: List<LineItem>
    ): Order {
        return orders.save(Order(location = location, items = items))
    }
}