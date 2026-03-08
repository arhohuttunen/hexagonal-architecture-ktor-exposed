package com.arhohuttunen.coffeeshop.domain

import kotlinx.datetime.YearMonth

data class CreditCard(
    val cardHolderName: String,
    val cardNumber: String,
    val expiry: YearMonth
)
