package com.arhohuttunen.coffeeshop.application

import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryOrders
import com.arhohuttunen.coffeeshop.adapters.outbound.InMemoryPayments
import com.arhohuttunen.coffeeshop.application.ports.inbound.OrderingCoffee
import com.arhohuttunen.coffeeshop.application.ports.inbound.PreparingCoffee
import com.arhohuttunen.coffeeshop.application.ports.outbound.OrderNotFound
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.application.ports.outbound.Payments
import com.arhohuttunen.coffeeshop.domain.CreditCardTestFactory.aCreditCard
import com.arhohuttunen.coffeeshop.domain.Drink
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Milk
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.aPaidOrder
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.anOrder
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.anOrderInPreparation
import com.arhohuttunen.coffeeshop.domain.PaymentTestFactory.aPaymentForOrder
import com.arhohuttunen.coffeeshop.domain.Size
import com.arhohuttunen.coffeeshop.domain.Status
import io.kotest.assertions.throwables.shouldThrow
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
        order.items shouldContainExactly listOf(LineItem(Drink.CAPPUCCINO, Milk.SKIMMED, Size.SMALL, 1))
        order.status shouldBe Status.PAYMENT_EXPECTED
    }

    test("customer can update the order before paying") {
        val oneItem = listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 1))
        val twoItems = listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 2))

        val order = customer.placeOrder(location = Location.TAKE_AWAY, items = oneItem)
        val updatedOrder = customer.updateOrder(order.id, Location.IN_STORE, twoItems)

        updatedOrder.items shouldContainExactly twoItems
    }

    test("order cannot be updated if it has been paid") {
        val existingOrder = orders.save(aPaidOrder())

        shouldThrow<IllegalStateException> {
            customer.updateOrder(existingOrder.id, Location.TAKE_AWAY, emptyList())
        }
    }

    test("customer can cancel the order before paying") {
        val existingOrder = orders.save(anOrder())

        customer.cancelOrder(existingOrder.id)

        shouldThrow<OrderNotFound> {
            orders.findById(existingOrder.id)
        }
    }

    test("an order cannot be cancelled if it has been paid") {
        val existingOrder = orders.save(aPaidOrder())

        shouldThrow<IllegalStateException> {
            customer.cancelOrder(existingOrder.id)
        }
    }

    test("customer can pay the order") {
        val order = orders.save(anOrder())
        val creditCard = aCreditCard()

        val payment = customer.payOrder(order.id, creditCard)

        payment.orderId shouldBe order.id
        payment.creditCard shouldBe creditCard
        orders.findById(order.id).status shouldBe Status.PAID
    }

    test("customer can get a receipt when the order is paid") {
        val existingOrder = orders.save(aPaidOrder())
        val existingPayment = payments.save(aPaymentForOrder(existingOrder.id))

        val receipt = customer.readReceipt(existingOrder.id)

        receipt.amount shouldBe existingOrder.cost()
        receipt.paidAt shouldBe existingPayment.paidAt
    }

    test("barista can start preparing the order when it is paid") {
        val existingOrder = orders.save(aPaidOrder())

        val orderInPreparation = barista.startPreparingOrder(existingOrder.id)

        orderInPreparation.status shouldBe Status.PREPARING
    }

    test("barista can mark the order ready when they have finished preparing it") {
        val existingOrder = orders.save(anOrderInPreparation())

        val preparedOrder = barista.finishPreparingOrder(existingOrder.id)

        preparedOrder.status shouldBe Status.READY
    }
})