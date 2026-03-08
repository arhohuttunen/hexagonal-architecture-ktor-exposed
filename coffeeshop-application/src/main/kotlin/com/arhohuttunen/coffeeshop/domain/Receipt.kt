package com.arhohuttunen.coffeeshop.domain

import java.math.BigDecimal
import kotlin.time.Instant

data class Receipt(val amount: BigDecimal, val paidAt: Instant)
