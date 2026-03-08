package com.arhohuttunen.coffeeshop.domain

import com.arhohuttunen.coffeeshop.domain.CreditCardTestFactory.aCreditCard
import kotlin.time.Clock
import kotlin.uuid.Uuid

object PaymentTestFactory {
    fun aPaymentForOrder(orderId: Uuid) = Payment(orderId, aCreditCard(), Clock.System.now())
}