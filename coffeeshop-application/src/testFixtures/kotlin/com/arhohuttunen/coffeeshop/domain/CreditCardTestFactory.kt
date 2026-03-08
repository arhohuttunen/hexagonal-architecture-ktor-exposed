package com.arhohuttunen.coffeeshop.domain

import kotlinx.datetime.YearMonth

object CreditCardTestFactory {
    fun aCreditCard() = CreditCard("Michael Faraday", "11223344", YearMonth.parse("2027-02"))
}