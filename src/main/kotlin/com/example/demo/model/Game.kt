package com.example.demo.model

import org.hibernate.annotations.GenericGenerator
import java.time.LocalDate
import java.util.*
import javax.persistence.*


@Entity
data class Game(
  @Id
  @GeneratedValue
  @GenericGenerator(name = "id", strategy = "uuid2")
  val id: UUID = UUID.randomUUID(),
  val user1: Long,
  var user2: Long? = null,
  val createdAt: LocalDate = LocalDate.now(),
  var started: Boolean = false,
  var winner: Long? = null,
  var surrender: Boolean = false,
  var turn: Long? = null,

  @OneToMany(fetch = FetchType.EAGER)
  val ships: MutableSet<Ship> = mutableSetOf(),


  @OneToMany(fetch = FetchType.EAGER)
  val shots: MutableSet<Shot> = mutableSetOf(),
  var user1SetShips: Boolean = false,
  var user2SetShips: Boolean = false,
  val height: Int = 10,
  val width: Int = 10,
) {
  fun addShot(s: Shot) {
    shots.add(s)
  }

  fun addShips(newShips: List<Ship>, userId: Long) {
    if (user1 == userId) {
      user1SetShips = true
    } else {
      user2SetShips = true
    }
    ships.addAll(newShips)
  }

  fun getUsers(): List<Long> = listOf(user1, user2!!)

  fun getOpponentOf(id: Long): Long {
    return getUsers().first { it != id }
  }
}