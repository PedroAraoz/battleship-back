package com.example.demo.model

data class GameMessage(
  val senderId: String,
  val move: Move,
  val coords: Pair<Int, Int>? = null
)

enum class Move {
  // server moves
  GAME_START, GAME_END,
  // player moves
  SHOOT, HIT, MISS
}
