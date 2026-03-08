package com.arhohuttunen.coffeeshop.adapter.outbound.persistence

import com.arhohuttunen.coffeeshop.application.ports.outbound.Payments
import com.arhohuttunen.coffeeshop.domain.Payment
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.upsert
import kotlin.uuid.Uuid

object ExposedPaymentsRepository : Payments {
    override fun save(payment: Payment): Payment {
        transaction {
            PaymentsTable.upsert {
                it[PaymentsTable.orderId] = payment.orderId
                it[PaymentsTable.cardHolderName] = payment.creditCard.cardHolderName
                it[PaymentsTable.cardNumber] = payment.creditCard.cardNumber
                it[PaymentsTable.cardExpiry] = payment.creditCard.expiry.toString()
                it[PaymentsTable.paidAt] = payment.paidAt
            }
        }
        return payment
    }

    override fun findByOrderId(orderId: Uuid): Payment {
        TODO("Not yet implemented")
    }
}