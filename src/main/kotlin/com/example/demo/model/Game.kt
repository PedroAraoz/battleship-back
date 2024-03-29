package com.example.demo.model

import com.fasterxml.jackson.annotation.JsonIgnore
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
  var user1AutoShooting: Boolean = false,
  var user2AutoShooting: Boolean = false,

  @OneToMany(fetch = FetchType.EAGER)
  val ships: MutableSet<Ship> = mutableSetOf(),

  @OneToMany(fetch = FetchType.EAGER)
  val shots: MutableSet<Shot> = mutableSetOf(),
  var user1SetShips: Boolean = false,
  var user2SetShips: Boolean = false,
  val height: Int = 10,
  val width: Int = 10,
) {
  @JsonIgnore
  fun addShot(s: Shot) {
    shots.add(s)
  }

  @JsonIgnore
  fun addShips(newShips: List<Ship>, userId: Long) {
    if (user1 == userId) {
      user1SetShips = true
    } else {
      user2SetShips = true
    }
    ships.addAll(newShips)
  }

  @JsonIgnore
  fun getUsers(): List<Long> = listOf(user1, user2!!)

  @JsonIgnore
  fun getOpponentOf(id: Long): Long {
    return getUsers().first { it != id }
  }

  @JsonIgnore
  fun toggleAutoShooting(userId: Long): Boolean {
    return if (user1 == userId) {
      user1AutoShooting = !user1AutoShooting
      user1AutoShooting
    } else {
      user2AutoShooting = !user2AutoShooting
      user2AutoShooting
    }
  }
}