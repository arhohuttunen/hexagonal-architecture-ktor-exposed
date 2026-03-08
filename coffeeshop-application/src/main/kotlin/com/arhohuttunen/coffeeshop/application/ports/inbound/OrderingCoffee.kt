package com.arhohuttunen.coffeeshop.application.ports.inbound

import com.arhohuttunen.coffeeshop.domain.CreditCard
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.Payment
import com.arhohuttunen.coffeeshop.domain.Receipt
import kotlin.uuid.Uuid

interface OrderingCoffee {
    fun placeOrder(location: Location, items: List<LineItem>): Order
    fun updateOrder(orderId: Uuid, location: Location, items: List<LineItem>): Order
    fun cancelOrder(orderId: Uuid)
    fun payOrder(orderId: Uuid, creditCard: CreditCard): Payment
    fun readReceipt(orderId: Uuid): Receipt
}