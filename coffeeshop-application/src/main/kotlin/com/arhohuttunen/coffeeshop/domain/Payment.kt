package com.arhohuttunen.coffeeshop.domain

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class Payment(val orderId: Uuid, val creditCard: CreditCard, val paidAt: Instant)
