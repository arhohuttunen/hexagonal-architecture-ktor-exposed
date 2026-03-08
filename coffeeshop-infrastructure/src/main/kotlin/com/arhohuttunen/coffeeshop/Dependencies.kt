package com.arhohuttunen.coffeeshop

import com.arhohuttunen.coffeeshop.adapter.outbound.persistence.ExposedOrdersRepository
import com.arhohuttunen.coffeeshop.adapter.outbound.persistence.ExposedPaymentsRepository
import com.arhohuttunen.coffeeshop.adapter.outbound.persistence.ExposedTransactionScope
import com.arhohuttunen.coffeeshop.application.CoffeeMachine
import com.arhohuttunen.coffeeshop.application.CoffeeShop

class Dependencies {
    val orders = ExposedOrdersRepository
    val payments = ExposedPaymentsRepository
    val transactionScope = ExposedTransactionScope
    val orderingCoffee = CoffeeShop(orders, payments, transactionScope)
    val preparingCoffee = CoffeeMachine(orders, transactionScope)
}