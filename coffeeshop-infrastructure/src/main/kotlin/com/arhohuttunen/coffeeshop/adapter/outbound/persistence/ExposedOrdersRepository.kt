package com.arhohuttunen.coffeeshop.adapter.outbound.persistence

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.arhohuttunen.coffeeshop.application.ports.outbound.Orders
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.OrderError
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.upsert
import kotlin.uuid.Uuid

object ExposedOrdersRepository : Orders {
    override fun save(order: Order): Order {
        OrdersTable.upsert {
            it[OrdersTable.id] = order.id
            it[OrdersTable.location] = order.location
            it[OrdersTable.status] = order.status
        }

        OrderItemsTable.deleteWhere { OrderItemsTable.orderId eq order.id }
        OrderItemsTable.batchInsert(order.items) { item ->
            this[OrderItemsTable.orderId] = order.id
            this[OrderItemsTable.drink] = item.drink
            this[OrderItemsTable.milk] = item.milk
            this[OrderItemsTable.size] = item.size
            this[OrderItemsTable.quantity] = item.quantity
        }

        return order
    }

    override fun findById(orderId: Uuid): Either<OrderError, Order> {
        val orderRow = OrdersTable.selectAll()
            .where { OrdersTable.id eq orderId }
            .singleOrNull() ?: return OrderError.NotFound.left()

        val items = OrderItemsTable.selectAll()
            .where { OrderItemsTable.orderId eq orderId }
            .map { it.toLineItem() }

        return orderRow.toOrder(items).right()
    }

    override fun deleteById(orderId: Uuid) {
        OrderItemsTable.deleteWhere { this.orderId eq orderId }
        OrdersTable.deleteWhere { id eq orderId }
    }
}

private fun ResultRow.toLineItem() = LineItem(
    drink = this[OrderItemsTable.drink],
    milk = this[OrderItemsTable.milk],
    size = this[OrderItemsTable.size],
    quantity = this[OrderItemsTable.quantity],
)

private fun ResultRow.toOrder(items: List<LineItem>) = Order(
    id = this[OrdersTable.id],
    location = this[OrdersTable.location],
    items = items,
    status = this[OrdersTable.status]
)
