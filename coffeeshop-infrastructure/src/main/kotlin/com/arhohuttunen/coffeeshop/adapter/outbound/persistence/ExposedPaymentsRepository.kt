package com.arhohuttunen.coffeeshop.adapter.outbound.persistence

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.arhohuttunen.coffeeshop.application.ports.outbound.Payments
import com.arhohuttunen.coffeeshop.domain.CreditCard
import com.arhohuttunen.coffeeshop.domain.OrderError
import com.arhohuttunen.coffeeshop.domain.Payment
import kotlinx.datetime.YearMonth
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.upsert
import kotlin.uuid.Uuid

object ExposedPaymentsRepository : Payments {
    override fun save(payment: Payment): Payment {
        PaymentsTable.upsert {
            it[PaymentsTable.orderId] = payment.orderId
            it[PaymentsTable.cardHolderName] = payment.creditCard.cardHolderName
            it[PaymentsTable.cardNumber] = payment.creditCard.cardNumber
            it[PaymentsTable.cardExpiry] = payment.creditCard.expiry.toString()
            it[PaymentsTable.paidAt] = payment.paidAt
        }
        return payment
    }

    override fun findByOrderId(orderId: Uuid): Either<OrderError, Payment> {
        val paymentRow = PaymentsTable.selectAll()
            .where { PaymentsTable.orderId eq orderId }
            .singleOrNull() ?: return OrderError.PaymentNotFound.left()

        return paymentRow.toPayment().right()
    }
}

private fun ResultRow.toPayment() = Payment(
    orderId = this[PaymentsTable.orderId],
    creditCard = CreditCard(
        this[PaymentsTable.cardHolderName],
        this[PaymentsTable.cardNumber],
        YearMonth.parse(this[PaymentsTable.cardExpiry])
    ),
    paidAt = this[PaymentsTable.paidAt]
)
