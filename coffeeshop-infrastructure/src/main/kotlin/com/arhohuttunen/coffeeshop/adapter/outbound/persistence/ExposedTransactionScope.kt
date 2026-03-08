package com.arhohuttunen.coffeeshop.adapter.outbound.persistence

import com.arhohuttunen.coffeeshop.application.ports.outbound.TransactionScope
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object ExposedTransactionScope : TransactionScope {
    override fun <T> execute(block: () -> T): T = transaction { block() }
}
