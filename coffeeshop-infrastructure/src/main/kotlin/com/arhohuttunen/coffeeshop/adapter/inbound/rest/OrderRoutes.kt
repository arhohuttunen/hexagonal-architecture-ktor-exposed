package com.arhohuttunen.coffeeshop.adapter.inbound.rest

import com.arhohuttunen.coffeeshop.application.ports.inbound.OrderingCoffee
import com.arhohuttunen.coffeeshop.application.ports.inbound.PreparingCoffee
import com.arhohuttunen.coffeeshop.domain.Drink
import com.arhohuttunen.coffeeshop.domain.LineItem
import com.arhohuttunen.coffeeshop.domain.Location
import com.arhohuttunen.coffeeshop.domain.Milk
import com.arhohuttunen.coffeeshop.domain.Order
import com.arhohuttunen.coffeeshop.domain.Size
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.href
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import kotlin.uuid.Uuid

@Serializable
@Resource("/orders")
class Orders {
    @Serializable
    @Resource("{id}")
    data class ById(val parent: Orders = Orders(), val id: Uuid) {
        @Serializable
        @Resource("preparation")
        data class Preparation(val parent: ById)
    }
}

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

fun Route.orderRoutes(orderingCoffee: OrderingCoffee, preparingCoffee: PreparingCoffee) {
    post<Orders> {
        val request = call.receive<OrderRequest>()
        val order = orderingCoffee.placeOrder(request.location, request.domainItems())
        call.response.headers.append(HttpHeaders.Location, application.href(Orders.ById(id = order.id)))
        call.respond(HttpStatusCode.Created, OrderResponse.fromDomain(order))
    }
    put<Orders.ById> { resource ->
        val request = call.receive<OrderRequest>()
        orderingCoffee.updateOrder(resource.id, request.location, request.domainItems())
            .fold(
                { call.respondError(it) },
                { call.respond(HttpStatusCode.OK, OrderResponse.fromDomain(it)) }
            )
    }
    delete<Orders.ById> { resource ->
        orderingCoffee.cancelOrder(resource.id)
            .fold(
                { call.respondError(it) },
                { call.respond(HttpStatusCode.NoContent) }
            )
    }
    put<Orders.ById.Preparation> { resource ->
        preparingCoffee.startPreparingOrder(resource.parent.id)
            .fold(
                { call.respondError(it) },
                { call.respond(HttpStatusCode.OK, OrderResponse.fromDomain(it)) }
            )
    }
    delete<Orders.ById.Preparation> { resource ->
        preparingCoffee.finishPreparingOrder(resource.parent.id)
            .fold(
                { call.respondError(it) },
                { call.respond(HttpStatusCode.OK, OrderResponse.fromDomain(it)) }
            )
    }
}
