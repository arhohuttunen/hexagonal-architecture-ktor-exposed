package com.arhohuttunen.coffeeshop.application

import com.arhohuttunen.coffeeshop.application.ports.inbound.OrderingCoffee
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Order
import kotlin.uuid.Uuid

class CoffeeShop(private val orders: Orders) : OrderingCoffee {
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
}