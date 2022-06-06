package com.example.demo.repository

import com.example.demo.model.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GameRepository : JpaRepository<Game, UUID> {

  @Query(
    value =
    "SELECT * FROM Game g\n" +
      "WHERE g.user1 != ?1\n" +
      "AND g.user2 IS NULL\n" +
      "ORDER BY g.created_at ASC;",
    nativeQuery = true
  )
  fun findEmptyGame(userId: Long): List<Game>

  @Query(
    value =
    "SELECT * FROM Game g\n" +
      "WHERE g.started = 'true'\n" +
      "AND g.winner IS NULL\n" +
      "AND (g.user1 = ?1 or g.user2 = ?1);",
    nativeQuery = true
  )
  fun getActiveGame(userId: Long): Game?
}