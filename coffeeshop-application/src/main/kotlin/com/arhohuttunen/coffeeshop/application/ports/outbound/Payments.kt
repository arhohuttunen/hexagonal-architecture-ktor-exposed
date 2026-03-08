package com.arhohuttunen.coffeeshop.application.ports.outbound

import com.arhohuttunen.coffeeshop.domain.Payment

interface Payments {
    fun save(payment: Payment): Payment
}