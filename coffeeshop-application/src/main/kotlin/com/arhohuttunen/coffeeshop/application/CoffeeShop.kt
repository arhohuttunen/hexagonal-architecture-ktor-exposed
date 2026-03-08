package com.arhohuttunen.coffeeshop.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.arhohuttunen.coffeeshop.application.ports.inbound.OrderingCoffee
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.application.ports.outbound.Payments
import com.arhohuttunen.coffeeshop.application.ports.outbound.TransactionScope
import com.arhohuttunen.coffeeshop.domain.CreditCard
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.OrderError
import com.arhohuttunen.coffeeshop.domain.Payment
import com.arhohuttunen.coffeeshop.domain.Receipt
import kotlin.time.Clock
import kotlin.uuid.Uuid

class CoffeeShop(
    private val orders: Orders,
    private val payments: Payments,
    private val transactionScope: TransactionScope = TransactionScope()
) : OrderingCoffee {
    override fun placeOrder(location: Location, items: List<LineItem>): Order =
        transactionScope.execute {
            orders.save(Order.Placed(location = location, items = items))
        }

    override fun updateOrder(orderId: Uuid, location: Location, items: List<LineItem>): Either<OrderError, Order> =
        transactionScope.execute {
            either {
                val order = orders.findById(orderId).bind()
                ensure(order is Order.Placed) { OrderError.AlreadyPaid }
                orders.save(order.update(location, items))
            }
        }

    override fun cancelOrder(orderId: Uuid): Either<OrderError, Unit> =
        transactionScope.execute {
            either {
                val order = orders.findById(orderId).bind()
                ensure(order is Order.Placed) { OrderError.AlreadyPaid }
                orders.deleteById(orderId)
            }
        }

    override fun payOrder(orderId: Uuid, creditCard: CreditCard): Either<OrderError, Payment> =
        transactionScope.execute {
            either {
                val order = orders.findById(orderId).bind()
                ensure(order is Order.Placed) { OrderError.AlreadyPaid }
                orders.save(order.pay())
                payments.save(Payment(orderId, creditCard, Clock.System.now()))
            }
        }

    override fun readReceipt(orderId: Uuid): Either<OrderError, Receipt> =
        transactionScope.execute {
            either {
                val order = orders.findById(orderId).bind()
                val payment = payments.findByOrderId(orderId).bind()
                Receipt(order.cost(), payment.paidAt)
            }
        }

    override fun takeOrder(orderId: Uuid): Either<OrderError, Order> =
        transactionScope.execute {
            either {
                val order = orders.findById(orderId).bind()
                ensure(order is Order.Ready) { OrderError.NotReady }
                orders.save(order.take())
            }
        }
}
