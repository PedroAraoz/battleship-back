package com.example.demo.model

import org.hibernate.annotations.GenericGenerator
import java.time.LocalDate
import java.util.*
import javax.persistence.*


@Entity
data class Game(
  @Id
  @GeneratedValue
  @GenericGenerator(name = "id", strategy = "uuid2")
  val id: UUID? = null,
  val user1: Long,
  var user2: Long? = null,
  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  var messages: MutableList<Message> = mutableListOf(),
  val createdAt: LocalDate = LocalDate.now(),
  val active: Boolean = true,
) {
  fun addMessage(message: Message) = messages.add(message)
}