package com.example.demo.model

data class GameGrid(
  val gridBlocks: List<List<GameGridPoint>>
)

data class GameGridPoint(
  val type: GameGridPointType = GameGridPointType.WATER,
  val fired: Boolean = false,
)

enum class GameGridPointType {
  WATER, SHIP
}