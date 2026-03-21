package com.arhohuttunen.coffeeshop.adapter.outbound.persistence

import com.arhohuttunen.coffeeshop.domain.CreditCard
import com.arhohuttunen.coffeeshop.domain.Payment
import com.arhohuttunen.coffeeshop.domain.aCreditCard
import com.arhohuttunen.coffeeshop.domain.anOrder
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerProjectExtension
import io.kotest.matchers.shouldBe
import kotlinx.datetime.YearMonth
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.time.Instant
import kotlin.uuid.Uuid

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

    test("saving a payment persists its data to the database") {
        transaction {
            val order = anOrder()
            val payment = Payment(
                order.id,
                CreditCard("Michael Faraday", "11223344", YearMonth.parse("2027-02")),
                Instant.parse("2026-03-21T08:00:00Z")
            )

            existing(order)

            ExposedPaymentsRepository.save(payment)

            val row = persistedPaymentRow(payment.orderId)
            row[PaymentsTable.cardHolderName] shouldBe "Michael Faraday"
            row[PaymentsTable.cardNumber] shouldBe "11223344"
            row[PaymentsTable.cardExpiry] shouldBe "2027-02"
            row[PaymentsTable.paidAt] shouldBe Instant.parse("2026-03-21T08:00:00Z")
        }
    }

    test("finding previously made payment returns its details") {
        transaction {
            val order = anOrder()
            val creditCard = aCreditCard()

            existing(order)
            existing(Payment(order.id, creditCard, Instant.parse("2026-03-20T10:00:00Z")))

            val result = ExposedPaymentsRepository.findByOrderId(order.id)

            val payment = result.shouldBeRight()
            payment.creditCard shouldBe creditCard
            payment.paidAt shouldBe Instant.parse("2026-03-20T10:00:00Z")
        }
    }
})

fun existing(payment: Payment) {
    ExposedPaymentsRepository.save(payment)
}

private fun persistedPaymentRow(orderId: Uuid) =
    PaymentsTable.selectAll().where { PaymentsTable.orderId eq orderId }.single()
