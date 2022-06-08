package com.example.demo

import com.example.demo.model.*
import org.springframework.stereotype.Service
import java.awt.Point

@Service
class GameManager {

  val validShips = listOf(5, 4, 3, 3, 2).sorted()

  fun createShot(game: Game, shotMessage: ShotMessage, userId: Long): Shot {
    val pos = if (shotMessage.random)
      Point(
        (0 until game.width).random(),
        (0 until game.height).random()
      )
    else shotMessage.pos!!


    return Shot(
      pos = pos,
      userId = userId,
    )
  }


  fun calculateHit(game: Game, shot: Shot): Ship? {
    val ships = getOpponentShips(game, shot.userId)
    return ships.firstOrNull { it.collide(shot.pos) }
  }


  fun checkShotUniqueness(game: Game, shot: Shot, userId: Long): Boolean {
    val shots = getShotsFromUser(game, userId)
    return !shots.any { it.pos == shot.pos }
  }

  fun getShipsFromUser(game: Game, userId: Long): List<Ship> {
    return game.ships.filter { it.userId == userId }
  }

  private fun getOpponentShips(game: Game, userId: Long): List<Ship> {
    return game.ships.filter { it.userId != userId }
  }

  private fun getShotsFromUser(game: Game, userId: Long): List<Shot> {
    return game.shots.filter { it.userId == userId }
  }

  private fun getOpponentShots(game: Game, userId: Long): List<Shot> {
    return game.shots.filter { it.userId != userId }
  }

  fun validateShipPlacement(ships: List<Ship>): Boolean {
    return ships.map { it.size }.sorted() == validShips
    // todo add more validation
  }

  fun getShipsFromMessage(game: Game, placement: ShipPlacementMessage): List<Ship> {
    if (placement.random) return generateRandomShips(game)
    return placement.ships
  }

  private fun generateRandomShips(game: Game): List<Ship> {
    val h = game.height
    val w = game.width

    TODO("not yet implemented")
  }

  fun isTurn(game: Game, userId: Long): Boolean {
    return game.turn == userId
  }

  fun checkGameWinner(game: Game): Long? {
    val (user1Ships: List<Ship>, user2Ships: List<Ship>) = game.ships.partition { it.userId == game.user1}
    return if (user1Ships.none { it.health > 0 }) game.user2!!
    else if (user2Ships.none { it.health > 0 }) game.user1
    else null
  }
}