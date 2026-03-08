package com.arhohuttunen.coffeeshop.domain

import kotlin.time.Clock
import kotlin.uuid.Uuid

fun aPaymentForOrder(orderId: Uuid): Payment =
    Payment(orderId, aCreditCard(), Clock.System.now())
