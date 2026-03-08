package com.arhohuttunen.coffeeshop.adapter.outbound.persistence

import com.arhohuttunen.coffeeshop.domain.Drink
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Milk
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.Size
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerProjectExtension
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer

class ExposedOrdersRepositoryTest : FunSpec({
    val postgres = install(TestContainerProjectExtension(PostgreSQLContainer<Nothing>("postgres")))

    Database.connect(
        url = postgres.jdbcUrl,
        driver = postgres.driverClassName,
        user = postgres.username,
        password = postgres.password
    )

    transaction {
        SchemaUtils.create(OrdersTable, OrderItemsTable)
    }

    test("creating an order returns the persisted order") {
        val order = Order(
            location = Location.TAKE_AWAY,
            items = listOf(
                LineItem(Drink.LATTE, Milk.WHOLE, Size.SMALL, 1)
            )
        )

        val persistedOrder = ExposedOrdersRepository.save(order)

        persistedOrder.location shouldBe Location.TAKE_AWAY
        persistedOrder.items shouldContainExactly listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.SMALL, 1))
    }
})
