package com.arhohuttunen.coffeeshop

import com.arhohuttunen.coffeeshop.adapter.outbound.persistence.ExposedOrdersRepository
import com.arhohuttunen.coffeeshop.adapter.outbound.persistence.ExposedPaymentsRepository
import com.arhohuttunen.coffeeshop.application.CoffeeMachine
import com.arhohuttunen.coffeeshop.application.CoffeeShop

class Dependencies {
    val orders = ExposedOrdersRepository
    val payments = ExposedPaymentsRepository
    val orderingCoffee = CoffeeShop(orders, payments)
    val preparingCoffee = CoffeeMachine(orders)
}