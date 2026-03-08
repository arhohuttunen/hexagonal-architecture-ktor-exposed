package com.arhohuttunen.coffeeshop.adapter.outbound.persistence

import com.arhohuttunen.coffeeshop.domain.Drink
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Milk
import com.arhohuttunen.coffeeshop.domain.Size
import com.arhohuttunen.coffeeshop.domain.Status
import org.jetbrains.exposed.v1.core.Table

object OrdersTable : Table("orders") {
    val id = uuid("id")
    val location = enumerationByName<Location>("location", 20)
    val status = enumerationByName<Status>("status", 20)

    override val primaryKey = PrimaryKey(id)
}

object OrderItemsTable : Table("order_items") {
    val orderId = uuid("order_id").references(OrdersTable.id)
    val drink = enumerationByName<Drink>("drink", 20)
    val milk = enumerationByName<Milk>("milk", 20)
    val size = enumerationByName<Size>("size", 20)
    val quantity = integer("quantity")
}
