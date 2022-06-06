package com.example.demo.model

import java.awt.Point
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Shot(
  @Id @GeneratedValue
  val id: Long = 0,
  val pos: Point,
  val userId: Long,
  var hit: Boolean? = null
)