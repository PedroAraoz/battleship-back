package com.example.demo.model

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  val id: Long,
  val email: String,
  val firstName: String,
  val lastname: String,
  val imageUrl: String,
)
