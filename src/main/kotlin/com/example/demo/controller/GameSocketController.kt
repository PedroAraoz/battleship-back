package com.example.demo.controller

import com.example.demo.model.*
import com.example.demo.service.AuthenticationService
import com.example.demo.service.GameService
import com.example.demo.service.MessageService
import com.example.demo.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.server.ResponseStatusException


@Controller
class GameSocketController(
  @Autowired private val messageService: MessageService,
  @Autowired private val gameService: GameService,
  @Autowired private val userService: UserService,
  @Autowired private val simpMessagingTemplate: SimpMessagingTemplate,
  @Autowired private val authenticationService: AuthenticationService,
) {
  @MessageMapping("/game")
  fun processMessage(@Payload dto: MessageDTO) {
    // todo add message with kind and handlers for each kind
    val gameId = dto.gameId!!
    val userId = getAuthUserOrError(dto.token!!).id
    val game: Game = gameService.getGame(gameId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    //check if user can message in game
    if (!gameService.ifUserBelongs(userId, game)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    // todo check if game has started
    val message: Message = dto.toMessage(userId)
    val saved: Message = messageService.save(message)
    gameService.addMessage(game, saved)
    simpMessagingTemplate.convertAndSend(
      "/queue/messages/$gameId",
      message.toDTO()
    )
  }


  private fun getAuthUser(idToken: String): User? {
    val email = authenticationService.authenticate(idToken.replace("Bearer ", ""))
    return if (email != null)
      userService.getUserByEmail(email)
    else
      null
  }

  private fun getAuthUserOrError(idToken: String): User {
    return getAuthUser(idToken) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
  }
}


