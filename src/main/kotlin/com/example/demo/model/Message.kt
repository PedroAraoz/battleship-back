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
  private val id: Long,
  val senderId: Long,
  val gameUuid: UUID,
  val content: String,
  var timestamp: LocalDate,
  @ManyToOne
  @JsonIgnore
  private val game: Game? = null
)

data class MessageDTO(
  val gameId: UUID? = null,
  val token: String? = null,
  val content: String? = null,
  val timestamp: String? = null
)

fun MessageDTO.toMessage(senderId: Long) = Message(
  id = -1,
  senderId = senderId,
  gameUuid = this.gameId!!,
  content = this.content!!,
  timestamp = LocalDate.now(),
)

fun Message.toDTO() = MessageDTO(
  gameId = this.gameUuid,
  content = this.content,
  timestamp = this.timestamp.toString()
)