package com.example.demo.service

import com.example.demo.helper.printlnGreen
import com.example.demo.model.*
import com.example.demo.model.GameMessageType.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class MessagingService(
  @Autowired private val simpMessagingTemplate: SimpMessagingTemplate,
) {

  fun sendMessage(gameId: UUID, userId: Long, message: GameMessage) {
    printlnGreen("Sent :: $userId :: " + ObjectMapper().writeValueAsString(message))
    simpMessagingTemplate.convertAndSend(
      "/queue/messages/$gameId/$userId",
      message
    )
  }

  fun sendStartGameMessage(game: Game) {
    game.getUsers().forEach { simpleSendStartGameMessage(game, it) }
  }

  fun simpleSendStartGameMessage(game: Game, userId: Long) {
    sendMessage(
      game.id,
      userId,
      SimpleMessage(START)
    )
  }

  fun sendShotMessage(game: Game, shot: Shot) {
    game.getUsers().forEach {
      sendMessage(
        gameId = game.id,
        it,
        ShotResultMessage(shot)
      )
    }
  }

  fun sendBoard(id: UUID, userId: Long, ships: List<Ship>, shots: List<Shot>, autoShooting: Boolean) {
    val (your, opponent) = shots.partition { it.userId == userId }
    val message = BoardDataMessage(
      ships = ships,
      yourShots = your,
      opponentShots = opponent,
      autoShooting = autoShooting,
    )
    sendMessage(
      id,
      userId,
      message
    )
  }

  fun sendTurnStart(game: Game) {
    simpleSendTurnStart(game)
    simpleSendWaitingToOpponent(game)
  }

  fun simpleSendTurnStart(game: Game) {
    sendMessage(
      game.id,
      game.turn!!,
      SimpleMessage(TURN_START)
    )
  }

  fun simpleSendWaitingToOpponent(game: Game) =
    simpleSendWaiting(game, game.getOpponentOf(game.turn!!))

  fun simpleSendWaiting(game: Game, userId: Long) {
    sendMessage(
      game.id,
      userId,
      SimpleMessage(WAITING)
    )
  }

  fun sendEndGameMessage(game: Game) {
    game.getUsers().forEach { simpleSendEndGameMessage(game, it) }
  }

  fun simpleSendEndGameMessage(game: Game, userId: Long) {
    sendMessage(
      game.id,
      userId,
      WinnerMessage(winner = game.winner!!, surrender = game.surrender))
  }
}