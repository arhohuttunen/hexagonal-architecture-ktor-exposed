package com.arhohuttunen.coffeeshop.domain

object OrderTestFactory {
    fun anOrder() =
        Order(
            location = Location.TAKE_AWAY,
            items = listOf(
                LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 1)
            )
        )

    fun aPaidOrder() =
        anOrder().markPaid()
}