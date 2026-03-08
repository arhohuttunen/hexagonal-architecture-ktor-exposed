package com.arhohuttunen.coffeeshop.domain

sealed class OrderError {
    data object NotFound : OrderError()
    data object PaymentNotFound : OrderError()
    data object AlreadyPaid : OrderError()
    data object NotPaid : OrderError()
    data object NotBeingPrepared : OrderError()
    data object NotReady : OrderError()
}
