package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.application.ports.inbound.OrderingCoffee
import com.arhohuttunen.coffeeshop.domain.Drink
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Milk
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.Size
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class LineItemRequest(
    val drink: Drink,
    val milk: Milk,
    val size: Size,
    val quantity: Int
) {
    fun toDomain() = LineItem(drink, milk, size, quantity)
}

@Serializable
data class LineItemResponse(
    val drink: Drink,
    val milk: Milk,
    val size: Size,
    val quantity: Int
) {
    companion object {
        fun fromDomain(lineItem: LineItem) =
            LineItemResponse(
                drink = lineItem.drink,
                milk = lineItem.milk,
                size = lineItem.size,
                quantity = lineItem.quantity
            )
    }
}

@Serializable
data class OrderRequest(
    val location: Location,
    val items: List<LineItemRequest>,
) {
    fun domainItems() = items.map(LineItemRequest::toDomain)
}

@Serializable
data class OrderResponse(
    val location: Location,
    val items: List<LineItemResponse>,
    @Serializable(with = BigDecimalSerializer::class)
    val cost: BigDecimal
) {
    companion object {
        fun fromDomain(order: Order) =
            OrderResponse(
                order.location,
                order.items.map(LineItemResponse::fromDomain),
                order.cost()
            )
    }
}

fun Route.orderRoutes(orderingCoffee: OrderingCoffee) {
    post("/orders") {
        val request = call.receive<OrderRequest>()
        val order = orderingCoffee.placeOrder(request.location, request.domainItems())
        call.response.headers.append(HttpHeaders.Location, "${call.request.uri}/${order.id}")
        call.respond(HttpStatusCode.Created, OrderResponse.fromDomain(order))
    }
}