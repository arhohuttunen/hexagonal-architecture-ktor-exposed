package com.arhohuttunen.coffeeshop.adapter.outbound.persistence

import com.arhohuttunen.coffeeshop.domain.CreditCardTestFactory.aCreditCard
import com.arhohuttunen.coffeeshop.domain.OrderTestFactory.anOrder
import com.arhohuttunen.coffeeshop.domain.Payment
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
        val order = anOrder()
        val creditCard = aCreditCard()
        val now = Clock.System.now()
        val payment = Payment(order.id, creditCard, now)
        existing(order)

        val persistedPayment = ExposedTransactionScope.execute { ExposedPaymentsRepository.save(payment) }

        persistedPayment.creditCard shouldBe creditCard
        persistedPayment.paidAt shouldBe now
    }

    test("finding previously made payment returns its details") {
        val order = anOrder()
        val creditCard = aCreditCard()
        val now = Clock.System.now()
        existing(order)
        existing(Payment(order.id, creditCard, now))

        val payment = ExposedTransactionScope.execute { ExposedPaymentsRepository.findByOrderId(order.id) }

        payment.creditCard shouldBe creditCard
        payment.paidAt shouldBe now
    }
})

fun existing(payment: Payment) {
    ExposedTransactionScope.execute { ExposedPaymentsRepository.save(payment) }
}
