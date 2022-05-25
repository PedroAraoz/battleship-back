package com.example.demo.controller

import com.example.demo.model.Chat
import com.example.demo.model.Message
import com.example.demo.model.User
import com.example.demo.service.ChatService
import com.example.demo.service.MessageService
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
class ChatController(
  @Autowired private val messageService: MessageService,
  @Autowired private val chatService: ChatService,
  @Autowired private val userService: UserService,
) {

  @PostMapping("/chat/join")
  fun joinChat(): String {
    val user = getAuthUserOrError()
    return chatService.joinOrCreateChat(user)
  }

  @GetMapping("/chat/started/{chatId}")
  fun hasChatStarted(@PathVariable chatId: UUID): Boolean {
    val user = getAuthUserOrError()
    val chat: Chat = chatService.getChat(chatId)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    if (!chatService.ifUserBelongs(user.id, chat)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    return chatService.hasChatStarted(chat)
  }

  @GetMapping("/chat/{chatId}")
  fun getChat(@PathVariable chatId: UUID): Chat {
    val user = getAuthUserOrError()
    val chat: Chat = chatService.getChat(chatId)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    if (!chatService.ifUserBelongs(user.id, chat)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    return chat
  }

  @GetMapping("/messages/{chatId}")
  fun findChatMessages(@PathVariable chatId: UUID): List<Message> {
    return messageService.findChatMessages(chatId)
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