package com.example.demo.service

import com.example.demo.model.Message
import com.example.demo.repository.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class MessageService(
  @Autowired private val messageRepository: MessageRepository,
  @Autowired private val gameService: GameService,
) {

  fun save(message: Message): Message {
    return messageRepository.save(message)
  }

  fun findGameMessages(gameId: UUID): List<Message> {
    return gameService.findById(gameId)?.messages ?: listOf()
  }
}