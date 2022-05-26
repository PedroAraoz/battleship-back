package com.example.demo.controller

import com.example.demo.model.*
import com.example.demo.service.ChatService
import com.example.demo.service.MessageService
import com.example.demo.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.server.ResponseStatusException


@Controller
class ChatSocketController(
  @Autowired private val messageService: MessageService,
  @Autowired private val chatService: ChatService,
  @Autowired private val userService: UserService,
  @Autowired private val simpMessagingTemplate: SimpMessagingTemplate
) {
  @MessageMapping("/chat/")
  fun processMessage(@DestinationVariable chatId: String, @Payload dto: MessageDTO) {
    val userId = getAuthUserOrError().id
    val chat: Chat = chatService.getChat(dto.chatId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    //check if user can message in chat
    if (!chatService.ifUserBelongs(userId, chat)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

    val message: Message = dto.toMessage(userId)
    val saved: Message = messageService.save(message)
    chatService.addMessage(chat, saved)
    simpMessagingTemplate.convertAndSend(
      "/queue/messages/$chatId",
      message.toDTO()
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


