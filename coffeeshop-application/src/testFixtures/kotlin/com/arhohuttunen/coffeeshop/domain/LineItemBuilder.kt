package com.arhohuttunen.coffeeshop.domain

class LineItemBuilder {
    var drink: Drink = Drink.LATTE
    var milk: Milk = Milk.WHOLE
    var size: Size = Size.LARGE
    var quantity: Int = 1
}

fun aLineItem(configure: LineItemBuilder.() -> Unit = {}): LineItem =
    LineItemBuilder().apply(configure).run { LineItem(drink, milk, size, quantity) }
