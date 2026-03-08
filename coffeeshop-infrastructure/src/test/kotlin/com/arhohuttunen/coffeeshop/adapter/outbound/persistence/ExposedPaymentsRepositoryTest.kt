package com.arhohuttunen.coffeeshop.adapter.outbound.persistence

import com.arhohuttunen.coffeeshop.domain.aCreditCard
import com.arhohuttunen.coffeeshop.domain.anOrder
import com.arhohuttunen.coffeeshop.domain.Payment
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerProjectExtension
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.time.Clock

class ExposedPaymentsRepositoryTest : FunSpec({
    val postgres = install(TestContainerProjectExtension(PostgreSQLContainer<Nothing>("postgres")))

    Database.connect(
        url = postgres.jdbcUrl,
        driver = postgres.driverClassName,
        user = postgres.username,
        password = postgres.password
    )

    transaction {
        SchemaUtils.create(OrdersTable, OrderItemsTable, PaymentsTable)
    }

    test("creating a payment returns the persisted payment") {
        transaction {
            val order = anOrder()
            val creditCard = aCreditCard()
            val now = Clock.System.now()
            val payment = Payment(order.id, creditCard, now)
            havingPersisted(order)

            val persistedPayment = ExposedPaymentsRepository.save(payment)

            persistedPayment.creditCard shouldBe creditCard
            persistedPayment.paidAt shouldBe now
        }
    }

    test("finding previously made payment returns its details") {
        transaction {
            val order = anOrder()
            val creditCard = aCreditCard()
            val now = Clock.System.now()
            havingPersisted(order)
            havingPersisted(Payment(order.id, creditCard, now))

            val payment = ExposedPaymentsRepository.findByOrderId(order.id).shouldBeRight()

            payment.creditCard shouldBe creditCard
            payment.paidAt shouldBe now
        }
    }
})

fun havingPersisted(payment: Payment) {
    ExposedPaymentsRepository.save(payment)
}
