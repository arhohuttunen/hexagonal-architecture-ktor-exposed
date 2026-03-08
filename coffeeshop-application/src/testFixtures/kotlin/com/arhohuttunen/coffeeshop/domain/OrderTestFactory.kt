package com.arhohuttunen.coffeeshop.domain

object OrderTestFactory {
    fun anOrder() =
        Order.Placed(
            location = Location.TAKE_AWAY,
            items = listOf(
                LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 1)
            )
        )

    fun aPaidOrder(): Order.Paid = anOrder().pay()

    fun anOrderInPreparation(): Order.InPreparation = aPaidOrder().startPreparing()

    fun aReadyOrder(): Order.Ready = anOrderInPreparation().finishPreparing()
}
