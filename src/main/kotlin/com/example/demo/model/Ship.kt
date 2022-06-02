package com.example.demo.model

data class Ship(
  val type: ShipType,
//  val position: ?
)

data class ShipType(
  val name: String,
  val size: Number
)

val smallShip = ShipType(name = "small", size = 1)
val mediumShip = ShipType(name = "medium", size = 2)
val bigShip = ShipType(name = "big", size = 3)


