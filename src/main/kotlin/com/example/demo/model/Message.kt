package com.example.demo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class Message(
  @Id
  @GeneratedValue
  val id: Long,
  val senderId: Long,
  val chatUuid: UUID,
  val content: String,
  var timestamp: LocalDate,
  @ManyToOne
  @JsonIgnore
  private val chat: Chat? = null
)

data class MessageDTO(
  val chatId: UUID,
  val content: String,
)

fun MessageDTO.toMessage(senderId: Long) = Message(
  id = -1,
  senderId = senderId,
  chatUuid = this.chatId,
  content = this.content,
  timestamp = LocalDate.now(),
)