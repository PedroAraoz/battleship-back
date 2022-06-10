package com.example.demo.controller

import com.example.demo.model.Game
import com.example.demo.model.SimpleResponse
import com.example.demo.model.User
import com.example.demo.service.GameService
import com.example.demo.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class GameController(
  @Autowired private val gameService: GameService,
  @Autowired private val userService: UserService,
) {

  @PostMapping("/game/join")
  fun joinGame(): SimpleResponse {
    val user = getAuthUserOrError()
    val id = UUID.fromString(gameService.joinOrCreateGame(user))
    val hasStarted = hasGameStarted(id).res.toBooleanStrict()
    // return the id of the game
    try {
      return SimpleResponse(id.toString())
    } finally {
      // if game has already started we need to notify the participants
      if (hasStarted) {
        Thread.sleep(1_000) // wait a second
        gameService.startGame(id)
      }
    }
  }

  @GetMapping("/game/started/{gameId}")
  fun hasGameStarted(@PathVariable gameId: UUID): SimpleResponse {
    val user = getAuthUserOrError()
    val game: Game = gameService.getGame(gameId)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    if (!gameService.ifUserBelongs(user.id, game)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    return SimpleResponse(gameService.hasGameStarted(game))
  }

  @GetMapping("/game/{gameId}")
  fun getGame(@PathVariable gameId: UUID): Game {
    val user = getAuthUserOrError()
    val game: Game = gameService.getGame(gameId)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    if (!gameService.ifUserBelongs(user.id, game)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    return game
  }

  private fun getAuthUser(): User? {
    val principal = SecurityContextHolder.getContext().authentication.principal
      as org.springframework.security.core.userdetails.User
    return userService.getUserByEmail(principal.username)
  }

  private fun getAuthUserOrError(): User {
    return getAuthUser() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
  }
}