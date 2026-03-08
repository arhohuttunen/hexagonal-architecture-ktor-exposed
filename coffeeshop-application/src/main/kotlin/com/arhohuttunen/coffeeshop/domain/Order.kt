package com.arhohuttunen.coffeeshop.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
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

    fun update(location: Location, items: List<LineItem>): Either<OrderError, Order> =
        if (status == Status.PAID) OrderError.AlreadyPaid.left()
        else copy(location = location, items = items).right()

    fun markPaid(): Either<OrderError, Order> =
        if (status != Status.PAYMENT_EXPECTED) OrderError.AlreadyPaid.left()
        else copy(status = Status.PAID).right()

    fun markBeingPrepared(): Either<OrderError, Order> =
        if (status != Status.PAID) OrderError.NotPaid.left()
        else copy(status = Status.PREPARING).right()

    fun markPrepared(): Either<OrderError, Order> =
        if (status != Status.PREPARING) OrderError.NotBeingPrepared.left()
        else copy(status = Status.READY).right()

    fun markTaken(): Either<OrderError, Order> =
        if (status != Status.READY) OrderError.NotReady.left()
        else copy(status = Status.TAKEN).right()
}
