package com.arhohuttunen.coffeeshop.adapter.outbound.persistence

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestamp

object PaymentsTable : Table("payments") {
    val orderId = uuid("order_id").references(OrdersTable.id)
    val cardHolderName = varchar("card_holder_name", 255)
    val cardNumber = varchar("card_number", 255)
    val cardExpiry = varchar("card_expiry", 7)
    val paidAt = timestamp("paid_at")

    override val primaryKey = PrimaryKey(orderId)
}
