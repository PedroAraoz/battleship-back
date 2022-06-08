package com.example.demo.service

import com.example.demo.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class MessagingService(
  @Autowired private val simpMessagingTemplate: SimpMessagingTemplate,
) {

  fun sendMessage(gameId: UUID, userId: Long, message: GameMessage) {
    simpMessagingTemplate.convertAndSend(
      "/queue/messages/$gameId/$userId",
      message
    )
  }

  fun sendStartGameMessage(gameId: UUID, userIds: List<Long>, message: GameMessage) {
    userIds.forEach {
      sendMessage(
        gameId,
        it,
        message
      )
    }
  }

  fun reSendStartGameMessage(gameId: UUID, userId: Long) {
    sendStartGameMessage(gameId, listOf(userId), SimpleMessage(GameMessageType.START))
  }

  fun sendStartGameMessage(game: Game) {
    sendStartGameMessage(game.id, getUsers(game), SimpleMessage(GameMessageType.START))
  }

  fun sendShotMessage(game: Game, shot: Shot) {
    val users = getUsers(game)
    val message = ShotResultMessage(shot)
    users.forEach {
      sendMessage(
        gameId = game.id,
        it,
        message
      )
    }
  }

  private fun getUsers(game: Game) = listOf(game.user1, game.user2!!)
  fun sendBoard(id: UUID, userId: Long, ships: List<Ship>, shots: List<Shot>) {
    val (your, opponent) = shots.partition { it.userId == userId }
    val message = BoardDataMessage(
      ships = ships,
      yourShots = your,
      opponentShots = opponent,
    )
    sendMessage(
      id,
      userId,
      message
    )
  }

  fun sendTurnStart(game: Game) {
    sendMessage(
      game.id,
      game.turn!!,
      SimpleMessage(GameMessageType.TURN_START)
    )
  }

  fun sendEndGameMessage(game: Game, winner: Long) {
    val message = WinnerMessage(
      winner = winner
    )
    game.getUsers().forEach {
      sendMessage(game.id, it, message)
    }
  }

  fun reSendEndGameMessage(game: Game, userId: Long) {
    val message = WinnerMessage(
      winner = game.winner!!
    )
    sendMessage(game.id, userId, message)
  }

  fun sendWaitMessage(game: Game, userId: Long) {
    sendMessage(
      game.id,
      userId,
      SimpleMessage(GameMessageType.WAITING)
    )
  }
}