package com.arhohuttunen.coffeeshop.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class OrderCostTest : FunSpec({
    test("1 small drink costs 4.00") {
        val order = anOrder { items(aLineItem { size = Size.SMALL }) }

        order.cost() shouldBe BigDecimal("4.00")
    }

    test("1 large drink costs 5.00") {
        val order = anOrder { items(aLineItem { size = Size.LARGE }) }

        order.cost() shouldBe BigDecimal("5.00")
    }

    test("2 small drinks costs 8.00") {
        val order = anOrder { items(aLineItem { size = Size.SMALL; quantity = 2 }) }

        order.cost() shouldBe BigDecimal("8.00")
    }

    test("1 large and 1 small drinks costs 9.00") {
        val order = anOrder { items(aLineItem { size = Size.LARGE }, aLineItem { size = Size.SMALL }) }

        order.cost() shouldBe BigDecimal("9.00")
    }
})
