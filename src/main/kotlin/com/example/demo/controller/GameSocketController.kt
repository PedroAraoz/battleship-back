package com.example.demo.controller

import com.example.demo.model.*
import com.example.demo.service.GameService
import com.example.demo.service.MessageService
import com.example.demo.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.server.ResponseStatusException


@Controller
class GameSocketController(
  @Autowired private val messageService: MessageService,
  @Autowired private val gameService: GameService,
  @Autowired private val userService: UserService,
  @Autowired private val simpMessagingTemplate: SimpMessagingTemplate
) {
  @MessageMapping("/game/")
  fun processMessage(@Payload dto: MessageDTO) {
    val gameId = dto.gameId
    val userId = getAuthUserOrError().id
    val game: Game = gameService.getGame(gameId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    //check if user can message in game
    if (!gameService.ifUserBelongs(userId, game)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

    val message: Message = dto.toMessage(userId)
    val saved: Message = messageService.save(message)
    gameService.addMessage(game, saved)
    simpMessagingTemplate.convertAndSend(
      "/queue/messages/$gameId",
      message
    )
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


