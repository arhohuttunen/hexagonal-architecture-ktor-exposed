package com.arhohuttunen.coffeeshop.domain

import java.math.BigDecimal
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
) {
    fun cost(): BigDecimal =
        if (size == Size.SMALL) {
            BigDecimal("4.00")
        } else {
            BigDecimal("5.00")
        }.multiply(quantity.toBigDecimal())
}

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
    fun canBeCancelled() = status == Status.PAYMENT_EXPECTED

    fun cost() = items.map(LineItem::cost).reduce(BigDecimal::add)

    fun update(location: Location, items: List<LineItem>): Order {
        if (status == Status.PAID) {
            throw IllegalStateException("Order is already paid")
        }
        return copy(location = location, items = items)
    }

    fun markPaid(): Order {
        if (status != Status.PAYMENT_EXPECTED) {
            throw IllegalStateException("Order is already paid")
        }
        return copy(status = Status.PAID)
    }

    fun markBeingPrepared(): Order {
        if (status != Status.PAID) {
            throw IllegalStateException("Order is not paid")
        }
        return copy(status = Status.PREPARING)
    }
}


