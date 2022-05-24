package com.example.demo.model

data class LoginDTO(
  val email: String,
  val name: String,
  val imageUrl: String,
  val accessToken: String,
  val idToken: String,
  val refreshToken: String
)

data class LoginResponseDTO(
  val email: String,
  val idToken: String,
  val refreshToken: String
)

fun LoginDTO.toUser(): User = User(
  id = 0,
  name = this.name,
  email = this.email,
  imageUrl = this.imageUrl,
)

fun LoginDTO.toLoginResponseDTO(): LoginResponseDTO = LoginResponseDTO(
  email = this.email,
  idToken = this.idToken,
  refreshToken = this.refreshToken
)