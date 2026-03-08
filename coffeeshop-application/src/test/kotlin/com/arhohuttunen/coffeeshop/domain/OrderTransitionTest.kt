package com.arhohuttunen.coffeeshop.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class OrderTransitionTest : FunSpec({
    test("updating a placed order changes location and items") {
        val order = anOrder()
        val newItems = listOf(LineItem(Drink.ESPRESSO, Milk.SKIMMED, Size.SMALL, 2))

        val updated = order.update(Location.IN_STORE, newItems)

        updated.id shouldBe order.id
        updated.location shouldBe Location.IN_STORE
        updated.items shouldContainExactly newItems
    }

    test("paying a placed order produces a paid order") {
        val order = anOrder()

        val paid = order.pay()

        paid.id shouldBe order.id
        paid.location shouldBe order.location
        paid.items shouldContainExactly order.items
    }

    test("starting preparation of a paid order produces an order in preparation") {
        val order = aPaidOrder()

        val inPreparation = order.startPreparing()

        inPreparation.id shouldBe order.id
        inPreparation.location shouldBe order.location
        inPreparation.items shouldContainExactly order.items
    }

    test("finishing preparation produces a ready order") {
        val order = anOrderInPreparation()

        val ready = order.finishPreparing()

        ready.id shouldBe order.id
        ready.location shouldBe order.location
        ready.items shouldContainExactly order.items
    }

    test("taking a ready order produces a taken order") {
        val order = aReadyOrder()

        val taken = order.take()

        taken.id shouldBe order.id
        taken.location shouldBe order.location
        taken.items shouldContainExactly order.items
    }
})
