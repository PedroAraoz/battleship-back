package com.example.demo.service

import com.example.demo.model.*
import org.springframework.stereotype.Service
import java.awt.Point

@Service
class GameManager {

  private val debug = false
  val validShips = if (debug) listOf(2, 2).sorted() else listOf(5, 4, 3, 3, 2).sorted()

  fun createShot(game: Game, shotMessage: ShotMessage, userId: Long): Shot {
    val shots = getShotsFromUser(game, userId)
    val points = shots.map { it.pos }
    val pos = if (shotMessage.random)
      generateRandomShot(game, points)
    else shotMessage.pos!!


    return Shot(
      pos = pos,
      userId = userId,
    )
  }

  private fun generateRandomShot(game: Game, points: List<Point>): Point {
    var point = Point(
      (0 until game.width).random(),
      (0 until game.height).random()
    )
    while (point in points) {
      point = Point(
        (0 until game.width).random(),
        (0 until game.height).random()
      )
    }
    return point
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

  fun validateShipPlacement(game: Game, ships: List<Ship>): Boolean {
    val w = game.width
    val h = game.height
    val val1: Boolean = ships.map { it.size }.sorted() == validShips
    // todo check if w and h don't need flipping when x != y
    val val2: Boolean = ships.none {
      it.startPos.x >= h ||
        it.startPos.y >= w ||
        it.startPos.x < 0 ||
        it.startPos.y < 0
    }
    return val1 && val2
  }

  fun getShipsFromMessage(game: Game, placement: ShipPlacementMessage): List<Ship> {
    if (placement.random) return generateRandomShips(game)
    return placement.ships
  }

  private fun generateRandomShips(game: Game): List<Ship> {
    val h = game.height
    val w = game.width
    TODO("not implemented")
  }

  fun isTurn(game: Game, userId: Long): Boolean {
    return game.turn == userId
  }

  fun checkGameWinner(game: Game): Long? {
    val (user1Ships: List<Ship>, user2Ships: List<Ship>) = game.ships.partition { it.userId == game.user1 }
    return if (user1Ships.none { it.health > 0 }) game.user2!!
    else if (user2Ships.none { it.health > 0 }) game.user1
    else null
  }

  fun chooseRandomTurn(game: Game): Long {
    return if (debug) game.user1
    else game.getUsers().random()
  }

  fun surrender(game: Game, userId: Long) {
    game.winner = game.getOpponentOf(userId)
    game.surrender = true
  }

  fun userPlacedShips(game: Game, userId: Long): Boolean {
    return if (game.user1 == userId) game.user1SetShips
    else game.user2SetShips
  }

  fun shotInsideBoard(game: Game, shot: Shot): Boolean {
    // todo check if w and h don't need flipping when x != y
    val x = shot.pos.x
    val y = shot.pos.y
    return x < game.width && y < game.height && x >= 0 && y >= 0
  }
}