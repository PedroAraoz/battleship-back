package com.example.demo.service

import com.example.demo.helper.unwrap
import com.example.demo.model.Chat
import com.example.demo.model.Message
import com.example.demo.model.User
import com.example.demo.repository.ChatRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ChatService(
  @Autowired private val chatRepository: ChatRepository
) {
  fun addMessage(chat: Chat, message: Message) {
    chat.addMessage(message)
    chatRepository.save(chat)
  }

  fun findById(id: UUID): Chat? {
    return chatRepository.findById(id).unwrap()
  }

  // empty chat : chat with only 1 participant
  fun joinOrCreateChat(user: User): String {
    // check if user has active chat
    if (chatRepository.getActiveChat(user.id) != null)
      throw ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "user already has an active chat"
      )
    val emptyChats: List<Chat> = chatRepository.findEmptyChat(user.id)
    val chat: Chat
    //check if empty chat available
    if (emptyChats.isNotEmpty()) {
      chat = emptyChats[0]
      chat.user2 = user.id
    } else {
      //if not create new empty chat
      chat = Chat(user1 = user.id)
    }
    return chatRepository.save(chat).id.toString()
  }

  fun hasChatStarted(chat: Chat): Boolean {
    return chat.user2 != null
  }

  fun getChat(chatId: UUID): Chat? {
    return chatRepository.findById(chatId).unwrap()
  }

  fun ifUserBelongs(userId: Long, chat: Chat): Boolean {
    return chat.user1 == userId || chat.user2 == userId
  }
}