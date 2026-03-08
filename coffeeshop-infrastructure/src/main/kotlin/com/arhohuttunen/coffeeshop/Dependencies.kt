package com.arhohuttunen.coffeeshop

import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryOrders
import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryPayments
import com.arhohuttunen.coffeeshop.application.CoffeeMachine
import com.arhohuttunen.coffeeshop.application.CoffeeShop

class Dependencies {
    val orders = InMemoryOrders()
    val payments = InMemoryPayments()
    val orderingCoffee = CoffeeShop(orders, payments)
    val preparingCoffee = CoffeeMachine(orders)
}