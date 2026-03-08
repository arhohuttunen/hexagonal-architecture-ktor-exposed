package com.arhohuttunen.coffeeshop.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class OrderCostTest : FunSpec({
    test("1 small drink costs 4.00") {
        val order = Order(
            location = Location.TAKE_AWAY,
            items = listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.SMALL, 1))
        )

        order.cost() shouldBe BigDecimal("4.00")
    }

    test("1 large drink costs 5.00") {
        val order = Order(
            location = Location.TAKE_AWAY,
            items = listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 1))
        )

        order.cost() shouldBe BigDecimal("5.00")
    }

    test("2 small drinks costs 8.00") {
        val order = Order(
            location = Location.TAKE_AWAY,
            items = listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.SMALL, 2))
        )

        order.cost() shouldBe BigDecimal("8.00")
    }

    test("1 large and 1 small drinks costs 9.00") {
        val order = Order(
            location = Location.TAKE_AWAY,
            items = listOf(
                LineItem(Drink.LATTE, Milk.SKIMMED, Size.LARGE, 1),
                LineItem(Drink.ESPRESSO, Milk.SOY, Size.SMALL, 1)
            )
        )

        order.cost() shouldBe BigDecimal("9.00")
    }
})