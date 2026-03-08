package com.arhohuttunen.coffeeshop.domain

import kotlin.uuid.Uuid

enum class Drink {
    LATTE,
    ESPRESSO,
    CAPPUCCINO
}

enum class Milk {
    WHOLE,
    SKIMMED,
    SOY
}

enum class Size {
    SMALL,
    LARGE
}

data class LineItem(
    val drink: Drink,
    val milk: Milk,
    val size: Size,
    val quantity: Int
)

enum class Location {
    IN_STORE,
    TAKE_AWAY
}

enum class Status {
    PAYMENT_EXPECTED,
    PAID,
    PREPARING,
    READY,
    TAKEN
}

data class Order(
    val id: Uuid = Uuid.random(),
    val location: Location,
    val items: List<LineItem>,
    val status: Status = Status.PAYMENT_EXPECTED
) {
    fun update(location: Location, items: List<LineItem>) = copy(location = location, items = items)

    fun canBeCancelled() = status == Status.PAYMENT_EXPECTED

    fun markPaid() = copy(status = Status.PAID)
}


