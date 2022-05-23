package com.example.demo.model

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private val id: Long,
  val name: String,
  val email: String,
  val imageUrl: String,
)
