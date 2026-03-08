package com.arhohuttunen.coffeeshop.application

import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryOrders
import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryPayments
import com.arhohuttunen.coffeeshop.application.ports.inbound.OrderingCoffee
import com.arhohuttunen.coffeeshop.application.ports.inbound.PreparingCoffee
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.application.ports.outbound.Payments
import com.arhohuttunen.coffeeshop.domain.CreditCardTestFactory.aCreditCard
import com.arhohuttunen.coffeeshop.domain.Drink
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Milk
import com.arhohuttunen.coffeeshop.domain.OrderError
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.aPaidOrder
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.aReadyOrder
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.anOrder
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.anOrderInPreparation
import com.arhohuttunen.coffeeshop.domain.PaymentTestFactory.aPaymentForOrder
import com.arhohuttunen.coffeeshop.domain.Size
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class AcceptanceTests : FunSpec({
    val orders: Orders = InMemoryOrders()
    val payments: Payments = InMemoryPayments()
    val customer: OrderingCoffee = CoffeeShop(orders, payments)
    val barista: PreparingCoffee = CoffeeMachine(orders)

    test("customer can order coffee") {
        val orderItems = listOf(LineItem(Drink.CAPPUCCINO, Milk.SKIMMED, Size.SMALL, 1))

        val order = customer.placeOrder(Location.IN_STORE, orderItems)

        order.location shouldBe Location.IN_STORE
        order.items shouldContainExactly orderItems
    }

    test("customer can update the order before paying") {
        val oneItem = listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 1))
        val twoItems = listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 2))
        val order = customer.placeOrder(location = Location.TAKE_AWAY, items = oneItem)

        customer.updateOrder(order.id, Location.IN_STORE, twoItems).shouldBeRight().items shouldContainExactly twoItems
    }

    test("order cannot be updated if it has been paid") {
        val existingOrder = orders.save(aPaidOrder())

        customer.updateOrder(existingOrder.id, Location.TAKE_AWAY, emptyList()).shouldBeLeft() shouldBe OrderError.AlreadyPaid
    }

    test("customer can cancel the order before paying") {
        val existingOrder = orders.save(anOrder())

        customer.cancelOrder(existingOrder.id).shouldBeRight()

        orders.findById(existingOrder.id).shouldBeLeft() shouldBe OrderError.NotFound
    }

    test("an order cannot be cancelled if it has been paid") {
        val existingOrder = orders.save(aPaidOrder())

        customer.cancelOrder(existingOrder.id).shouldBeLeft() shouldBe OrderError.AlreadyPaid
    }

    test("customer can pay the order") {
        val order = orders.save(anOrder())
        val creditCard = aCreditCard()

        val payment = customer.payOrder(order.id, creditCard).shouldBeRight()

        payment.orderId shouldBe order.id
        payment.creditCard shouldBe creditCard
    }

    test("customer can get a receipt when the order is paid") {
        val existingOrder = orders.save(aPaidOrder())
        val existingPayment = payments.save(aPaymentForOrder(existingOrder.id))

        val receipt = customer.readReceipt(existingOrder.id).shouldBeRight()

        receipt.amount shouldBe existingOrder.cost()
        receipt.paidAt shouldBe existingPayment.paidAt
    }

    test("customer cannot get a receipt when payment is missing") {
        val existingOrder = orders.save(aPaidOrder())

        customer.readReceipt(existingOrder.id).shouldBeLeft() shouldBe OrderError.PaymentNotFound
    }

    test("barista can start preparing the order when it is paid") {
        val existingOrder = orders.save(aPaidOrder())

        barista.startPreparingOrder(existingOrder.id).shouldBeRight()
    }

    test("barista cannot start preparing an order that has not been paid") {
        val existingOrder = orders.save(anOrder())

        barista.startPreparingOrder(existingOrder.id).shouldBeLeft() shouldBe OrderError.NotPaid
    }

    test("barista can mark the order ready when they have finished preparing it") {
        val existingOrder = orders.save(anOrderInPreparation())

        barista.finishPreparingOrder(existingOrder.id).shouldBeRight()
    }

    test("barista cannot finish preparing an order that is not being prepared") {
        val existingOrder = orders.save(aPaidOrder())

        barista.finishPreparingOrder(existingOrder.id).shouldBeLeft() shouldBe OrderError.NotBeingPrepared
    }

    test("customer can take the order when it is ready") {
        val existingOrder = orders.save(aReadyOrder())

        customer.takeOrder(existingOrder.id).shouldBeRight()
    }

    test("customer cannot take an order that is not ready") {
        val existingOrder = orders.save(anOrderInPreparation())

        customer.takeOrder(existingOrder.id).shouldBeLeft() shouldBe OrderError.NotReady
    }
})
