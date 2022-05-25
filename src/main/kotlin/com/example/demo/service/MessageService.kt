package com.example.demo.service

import com.example.demo.model.Message
import com.example.demo.repository.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class MessageService(
  @Autowired private val messageRepository: MessageRepository,
  @Autowired private val chatService: ChatService,
) {

  fun save(message: Message): Message {
    return messageRepository.save(message)
  }

  fun findChatMessages(chatId: UUID): List<Message> {
    return chatService.findById(chatId)?.messages ?: listOf()
  }
}