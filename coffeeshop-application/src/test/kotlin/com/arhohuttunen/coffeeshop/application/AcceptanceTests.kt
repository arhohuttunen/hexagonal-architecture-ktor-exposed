package com.arhohuttunen.coffeeshop.application

import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryOrders
import com.arhohuttunen.coffeeshop.application.ports.inbound.OrderingCoffee
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.domain.Drink
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Milk
import com.arhohuttunen.coffeeshop.domain.Size
import com.arhohuttunen.coffeeshop.domain.Status
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class AcceptanceTests : FunSpec({
    val orders: Orders = InMemoryOrders()
    val customer: OrderingCoffee = CoffeeShop(orders)

    test("customer can order coffee") {
        val orderItems = listOf(LineItem(Drink.CAPPUCCINO, Milk.SKIMMED, Size.SMALL, 1))

        val order = customer.placeOrder(Location.IN_STORE, orderItems)

        order.location shouldBe Location.IN_STORE
        order.items shouldContainExactly listOf(LineItem(Drink.CAPPUCCINO, Milk.SKIMMED, Size.SMALL, 1))
        order.status shouldBe Status.PAYMENT_EXPECTED
    }

    test("customer can update the order before paying") {
        val oneItem = listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 1))
        val twoItems = listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 2))

        val order = customer.placeOrder(location = Location.TAKE_AWAY, items = oneItem)
        val updatedOrder = customer.updateOrder(order.id, Location.IN_STORE, twoItems)

        updatedOrder.items shouldContainExactly twoItems
    }
})