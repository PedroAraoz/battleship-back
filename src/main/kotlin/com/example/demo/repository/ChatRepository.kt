package com.example.demo.repository

import com.example.demo.model.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChatRepository : JpaRepository<Chat, UUID> {

  @Query(
    value =
    "SELECT * FROM Chat c\n" +
            "WHERE c.user1 != ?1\n" +
            "AND c.user2 IS NULL\n" +
            "ORDER BY c.created_at ASC;",
    nativeQuery = true
  )
  fun findEmptyChat(userId: Long): List<Chat>

  @Query(
    value =
    "SELECT * FROM Chat c\n" +
            "WHERE c.active = 'true'\n" +
            "AND (c.user1 = ?1 or c.user2 = ?1);",
    nativeQuery = true
  )
  fun getActiveChat(userId: Long): Chat?
}