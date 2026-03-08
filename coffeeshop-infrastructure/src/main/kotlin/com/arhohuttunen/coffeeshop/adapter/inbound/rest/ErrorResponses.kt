package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.domain.OrderError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend fun ApplicationCall.respondError(error: OrderError) = when (error) {
    OrderError.NotFound,
    OrderError.PaymentNotFound -> respond(HttpStatusCode.NotFound)

    OrderError.AlreadyPaid,
    OrderError.NotPaid,
    OrderError.NotBeingPrepared,
    OrderError.NotReady -> respond(HttpStatusCode.Conflict)
}
