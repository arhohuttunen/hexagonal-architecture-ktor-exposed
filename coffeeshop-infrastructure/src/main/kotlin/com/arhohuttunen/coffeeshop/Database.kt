package com.arhohuttunen.coffeeshop

import com.arhohuttunen.coffeeshop.adapter.outbound.persistence.OrderItemsTable
import com.arhohuttunen.coffeeshop.adapter.outbound.persistence.OrdersTable
import com.arhohuttunen.coffeeshop.adapter.outbound.persistence.PaymentsTable
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Application.configureDatabase(config: ApplicationConfig) {
    Database.connect(
        url = config.property("database.url").getString(),
        driver = config.property("database.driver").getString(),
        user = config.property("database.user").getString(),
        password = config.property("database.password").getString(),
    )

    transaction {
        SchemaUtils.create(OrdersTable, OrderItemsTable, PaymentsTable)
    }
}
