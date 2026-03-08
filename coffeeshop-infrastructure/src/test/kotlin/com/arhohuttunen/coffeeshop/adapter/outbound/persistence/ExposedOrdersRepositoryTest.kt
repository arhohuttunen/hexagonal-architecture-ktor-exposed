package com.arhohuttunen.coffeeshop.adapter.outbound.persistence

import com.arhohuttunen.coffeeshop.domain.Drink
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Milk
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.OrderError
import com.arhohuttunen.coffeeshop.domain.aPaidOrder
import com.arhohuttunen.coffeeshop.domain.anOrder
import com.arhohuttunen.coffeeshop.domain.anOrderInPreparation
import com.arhohuttunen.coffeeshop.domain.aReadyOrder
import com.arhohuttunen.coffeeshop.domain.Size
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
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
        transaction {
            val order = Order.Placed(
                location = Location.TAKE_AWAY,
                items = listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.SMALL, 1))
            )

            val persistedOrder = ExposedOrdersRepository.save(order)

            persistedOrder.location shouldBe Location.TAKE_AWAY
            persistedOrder.items shouldContainExactly listOf(LineItem(Drink.LATTE, Milk.WHOLE, Size.SMALL, 1))
        }
    }

    test("finding a placed order returns its details") {
        transaction {
            val orderId = havingPersisted(
                Order.Placed(
                    location = Location.IN_STORE,
                    items = listOf(LineItem(Drink.ESPRESSO, Milk.SKIMMED, Size.LARGE, 1))
                )
            )

            val order = ExposedOrdersRepository.findById(orderId).shouldBeRight()

            order.location shouldBe Location.IN_STORE
            order.items shouldContainExactly listOf(LineItem(Drink.ESPRESSO, Milk.SKIMMED, Size.LARGE, 1))
        }
    }

    test("finding a non-existing order returns NotFound") {
        transaction {
            val result = ExposedOrdersRepository.findById(Uuid.random())

            result.shouldBeLeft() shouldBe OrderError.NotFound
        }
    }

    test("deleting an order removes it") {
        transaction {
            val orderId = havingPersisted(anOrder())

            ExposedOrdersRepository.deleteById(orderId)

            val result = ExposedOrdersRepository.findById(orderId)

            result.shouldBeLeft() shouldBe OrderError.NotFound
        }
    }

    test("finding a paid order returns a paid order") {
        transaction {
            val orderId = havingPersisted(aPaidOrder())

            val result = ExposedOrdersRepository.findById(orderId)

            result.shouldBeRight().shouldBeInstanceOf<Order.Paid>()
        }
    }

    test("finding an order in preparation returns an order in preparation") {
        transaction {
            val orderId = havingPersisted(anOrderInPreparation())

            val result = ExposedOrdersRepository.findById(orderId)

            result.shouldBeRight().shouldBeInstanceOf<Order.InPreparation>()
        }
    }

    test("finding a ready order returns a ready order") {
        transaction {
            val orderId = havingPersisted(aReadyOrder())

            val result = ExposedOrdersRepository.findById(orderId)

            result.shouldBeRight().shouldBeInstanceOf<Order.Ready>()
        }
    }

    test("finding a taken order returns a taken order") {
        transaction {
            val orderId = havingPersisted(aReadyOrder().take())

            val result = ExposedOrdersRepository.findById(orderId)

            result.shouldBeRight().shouldBeInstanceOf<Order.Taken>()
        }
    }
})

fun havingPersisted(order: Order): Uuid {
    ExposedOrdersRepository.save(order)
    return order.id
}
