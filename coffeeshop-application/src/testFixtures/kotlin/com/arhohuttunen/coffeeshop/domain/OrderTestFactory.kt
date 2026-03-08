package com.arhohuttunen.coffeeshop.domain

object OrderTestFactory {
    fun anOrder() =
        Order(
            location = Location.TAKE_AWAY,
            items = listOf(
                LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 1)
            )
        )

    fun aPaidOrder() = anOrder().copy(status = Status.PAID)

    fun anOrderInPreparation() = anOrder().copy(status = Status.PREPARING)

    fun aReadyOrder() = anOrder().copy(status = Status.READY)
}
