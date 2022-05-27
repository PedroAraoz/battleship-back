package com.example.demo.controller

import com.example.demo.model.Game
import com.example.demo.model.Message
import com.example.demo.model.MessageDTO
import com.example.demo.model.User
import com.example.demo.service.GameService
import com.example.demo.service.MessageService
import com.example.demo.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class GameController(
  @Autowired private val messageService: MessageService,
  @Autowired private val gameService: GameService,
  @Autowired private val userService: UserService,
  @Autowired private val simpMessagingTemplate: SimpMessagingTemplate
) {

  @PostMapping("/game/join")
  fun joinGame(): String {
    val user = getAuthUserOrError()
    val id = UUID.fromString(gameService.joinOrCreateGame(user))
    val hasStarted = hasGameStarted(id)
    // return the id of the game
    try {
      return id.toString()
    } finally {
      // if game has already started we need to notify the participants
      if (hasStarted) {
        Thread.sleep(1_000) // wait a second
        simpMessagingTemplate.convertAndSend(
          "/queue/messages/$id",
          MessageDTO(gameId = id, content = "game started")
        )
      }
    }
  }

  @GetMapping("/game/started/{gameId}")
  fun hasGameStarted(@PathVariable gameId: UUID): Boolean {
    val user = getAuthUserOrError()
    val game: Game = gameService.getGame(gameId)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    if (!gameService.ifUserBelongs(user.id, game)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    return gameService.hasGameStarted(game)
  }

  @GetMapping("/game/{gameId}")
  fun getGame(@PathVariable gameId: UUID): Game {
    val user = getAuthUserOrError()
    val game: Game = gameService.getGame(gameId)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    if (!gameService.ifUserBelongs(user.id, game)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    return game
  }

  @GetMapping("/messages/{gameId}")
  fun findGameMessages(@PathVariable gameId: UUID): List<Message> {
    return messageService.findGameMessages(gameId)
      .sortedWith { m1, m2 -> m1.timestamp.compareTo(m2.timestamp) }
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