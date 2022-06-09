package com.example.demo.model

import java.awt.Point
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
data class Ship(
  @Id @GeneratedValue
  val id: Long = 0,
  val size: Int,
  var health: Int = size,
  val startPos: Point,
  val endPos: Point,
  var userId: Long? = null
) {
  fun hit() {
    health -= 1
  }

  fun collide(pos: Point): Boolean {
    val horizontalShip = startPos.y == endPos.y
    return if (horizontalShip) {
      (pos.x in (startPos.x..endPos.x) || pos.x in (endPos.x..startPos.x))
        && (pos.y == startPos.y)
    } else {
      (pos.y in (startPos.y..endPos.y) || pos.y in (endPos.y..startPos.y))
        && (pos.x == startPos.x)
    }
  }

}
