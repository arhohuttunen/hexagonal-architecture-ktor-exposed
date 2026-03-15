package com.arhohuttunen.coffeeshop.adapter.outbound.persistence

import com.arhohuttunen.coffeeshop.application.ports.outbound.OrderNotFound
import com.arhohuttunen.coffeeshop.domain.Drink
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Milk
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.anOrder
import com.arhohuttunen.coffeeshop.domain.Size
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerProjectExtension
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.uuid.Uuid

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

        val persistedOrder = ExposedTransactionScope.execute { ExposedOrdersRepository.save(order) }

        persistedOrder.location shouldBe Location.TAKE_AWAY
        persistedOrder.items shouldContainExactly listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.SMALL, 1))
    }

    test("finding a placed order returns its details") {
        val orderId = existing(
            Order(
                location = Location.IN_STORE,
                items = listOf(
                    LineItem(Drink.ESPRESSO, Milk.SKIMMED, Size.LARGE, 1)
                )
            )
        )

        val order = ExposedTransactionScope.execute { ExposedOrdersRepository.findById(orderId) }

        order.location shouldBe Location.IN_STORE
        order.items shouldContainExactly listOf(LineItem(Drink.ESPRESSO, Milk.SKIMMED, Size.LARGE, 1))
    }

    test("finding a non-existing order throws an exception") {
        shouldThrow<OrderNotFound> {
            ExposedTransactionScope.execute { ExposedOrdersRepository.findById(Uuid.random()) }
        }
    }

    test("deleting an order removes it") {
        val orderId = existing(anOrder())

        ExposedTransactionScope.execute { ExposedOrdersRepository.deleteById(orderId) }

        shouldThrow<OrderNotFound> {
            ExposedTransactionScope.execute { ExposedOrdersRepository.findById(orderId) }
        }
    }
})

fun existing(order: Order): Uuid {
    ExposedTransactionScope.execute { ExposedOrdersRepository.save(order) }
    return order.id
}
