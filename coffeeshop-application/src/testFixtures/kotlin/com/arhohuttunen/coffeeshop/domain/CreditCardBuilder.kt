package com.arhohuttunen.coffeeshop.domain

import kotlinx.datetime.YearMonth

class CreditCardBuilder {
    var cardHolderName: String = "Michael Faraday"
    var cardNumber: String = "11223344"
    var expiry: YearMonth = YearMonth.parse("2027-02")
}

fun aCreditCard(configure: CreditCardBuilder.() -> Unit = {}): CreditCard =
    CreditCardBuilder().apply(configure).run { CreditCard(cardHolderName, cardNumber, expiry) }
