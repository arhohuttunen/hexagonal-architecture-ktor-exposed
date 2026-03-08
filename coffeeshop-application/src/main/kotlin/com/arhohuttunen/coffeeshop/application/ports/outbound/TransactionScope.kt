package com.arhohuttunen.coffeeshop.application.ports.outbound

interface TransactionScope {
    fun <T> execute(block: () -> T): T

    companion object {
        operator fun invoke(): TransactionScope = object : TransactionScope {
            override fun <T> execute(block: () -> T): T = block()
        }
    }
}