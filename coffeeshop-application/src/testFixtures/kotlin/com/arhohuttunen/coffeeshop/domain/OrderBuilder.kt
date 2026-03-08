package com.arhohuttunen.coffeeshop.domain

class OrderBuilder {
    var location: Location = Location.TAKE_AWAY
    var items: List<LineItem> = listOf(aLineItem())
}

fun anOrder(configure: OrderBuilder.() -> Unit = {}): Order.Placed =
    OrderBuilder().apply(configure).run { Order.Placed(location = location, items = items) }

fun aPaidOrder(configure: OrderBuilder.() -> Unit = {}): Order.Paid = anOrder(configure).pay()

fun anOrderInPreparation(configure: OrderBuilder.() -> Unit = {}): Order.InPreparation = aPaidOrder(configure).startPreparing()

fun aReadyOrder(configure: OrderBuilder.() -> Unit = {}): Order.Ready = anOrderInPreparation(configure).finishPreparing()
