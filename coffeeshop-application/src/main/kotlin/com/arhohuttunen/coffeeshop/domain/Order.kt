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

sealed class Order {
    abstract val id: Uuid
    abstract val location: Location
    abstract val items: List<LineItem>

    fun cost() = items.map(LineItem::cost).reduce(BigDecimal::add)

    data class Placed(
        override val id: Uuid = Uuid.random(),
        override val location: Location,
        override val items: List<LineItem>
    ) : Order() {
        fun update(location: Location, items: List<LineItem>): Placed = copy(location = location, items = items)
        fun pay(): Paid = Paid(id, location, items)
    }

    data class Paid(
        override val id: Uuid,
        override val location: Location,
        override val items: List<LineItem>
    ) : Order() {
        fun startPreparing(): InPreparation = InPreparation(id, location, items)
    }

    data class InPreparation(
        override val id: Uuid,
        override val location: Location,
        override val items: List<LineItem>
    ) : Order() {
        fun finishPreparing(): Ready = Ready(id, location, items)
    }

    data class Ready(
        override val id: Uuid,
        override val location: Location,
        override val items: List<LineItem>
    ) : Order() {
        fun take(): Taken = Taken(id, location, items)
    }

    data class Taken(
        override val id: Uuid,
        override val location: Location,
        override val items: List<LineItem>
    ) : Order()
}
