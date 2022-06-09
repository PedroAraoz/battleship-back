package com.example.demo.service

import com.example.demo.controller.GameSocketController.MessageInfo
import com.example.demo.helper.unwrap
import com.example.demo.model.*
import com.example.demo.repository.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class GameService(
  @Autowired private val gameRepository: GameRepository,
  @Autowired private val messagingService: MessagingService,
  @Autowired private val gameManager: GameManager,
  @Autowired private val shipService: ShipService,
  @Autowired private val shotService: ShotService,
) {

  // empty game : game with only 1 participant
  fun joinOrCreateGame(user: User): String {
    // check if user has active game
    if (gameRepository.getActiveGame(user.id) != null)
      throw ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "user already has an active game"
      )
    val emptyGames: List<Game> = gameRepository.findEmptyGame(user.id)
    val game: Game
    //check if empty game is available
    if (emptyGames.isNotEmpty()) {
      game = emptyGames[0]
      game.user2 = user.id
    } else {
      //if not create new empty game
      game = Game(user1 = user.id)
    }
    return gameRepository.save(game).id.toString()
  }

  fun hasGameStarted(game: Game): Boolean {
    return game.user2 != null
  }

  fun getGame(gameId: UUID): Game? {
    return gameRepository.findById(gameId).unwrap()
  }

  fun getGameOrError(gameId: UUID): Game {
    return gameRepository.findById(gameId).unwrap()
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "game not found")
  }

  fun ifUserBelongs(userId: Long, game: Game): Boolean {
    return game.user1 == userId || game.user2 == userId
  }

  fun handleShipPlacement(shipPlacementMessage: ShipPlacementMessage, messageInfo: MessageInfo) {
    val game: Game = getGameOrError(messageInfo.gameId)
    val userId: Long = messageInfo.userId
    val ships: List<Ship> = gameManager.getShipsFromMessage(game, shipPlacementMessage)
    // todo check if user does not have ship placement
    if (!gameManager.validateShipPlacement(ships))
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid ship placement")
    ships.forEach { it.userId = userId }
    addShips(game, shipService.saveShips(ships), userId)

    val updatedGame = getGameOrError(game.id)
    val finishedPlacement = updatedGame.user1SetShips && updatedGame.user2SetShips
    if (finishedPlacement) {
      messagingService.sendTurnStart(game)
    }
  }

  fun handleShot(shotMessage: ShotMessage, messageInfo: MessageInfo) {
    val game = getGame(messageInfo)
    val userId = messageInfo.userId
    //check if it is the user turn
    if (!gameManager.isTurn(game, userId))
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "not your turn")
    val shot: Shot = gameManager.createShot(game, shotMessage, userId)
    // todo check if shot is inside board
    //check if shot is unique
    if (!gameManager.checkShotUniqueness(game, shot, userId))
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "you already shot there")
    val ship: Ship? = gameManager.calculateHit(game, shot)
    val isHit = ship != null
    shot.hit = isHit
    if (isHit) {
      ship!!.hit()
      shipService.saveShip(ship)
    }
    addShot(game, shot)
    val updatedGame = getGameOrError(game.id)
    messagingService.sendShotMessage(updatedGame, shot)
    val winner: Long? = gameManager.checkGameWinner(updatedGame)
    if (winner != null) {
      updatedGame.winner = winner
      val finalGame = gameRepository.save(updatedGame)
      messagingService.sendEndGameMessage(finalGame)
    }
    else changeTurn(updatedGame, userId)
  }

  private fun changeTurn(game: Game, currentUser: Long) {
    game.turn = game.getOpponentOf(currentUser)
    val updatedGame = gameRepository.save(game)
    messagingService.sendTurnStart(updatedGame)
  }

  fun getBoard(messageInfo: MessageInfo) {
    val game: Game = getGameOrError(messageInfo.gameId)
    val shots: List<Shot> = game.shots.toList()
    val ships: List<Ship> = gameManager.getShipsFromUser(game, messageInfo.userId)
    messagingService.sendBoard(game.id, messageInfo.userId, ships, shots)
  }

  private fun getGame(m: MessageInfo): Game {
    return getGame(m.gameId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND);
  }

  fun startGame(id: UUID) {
    val game: Game = getGameOrError(id)
    game.started = true
    game.turn = gameManager.chooseRandomTurn(game)
    gameRepository.save(game)
    messagingService.sendStartGameMessage(game)
  }

  fun addShips(g: Game, ships: List<Ship>, userId: Long) {
    g.addShips(ships, userId)
    gameRepository.save(g)
  }

  fun addShip(g: Game, s: Ship) {
    g.addShip(shipService.saveShip(s))
    gameRepository.save(g)
  }

  fun addShot(g: Game, shot: Shot) {
    g.addShot(shotService.saveShot(shot))
    gameRepository.save(g)
  }

  fun getState(messageInfo: MessageInfo) {
    val gameId = messageInfo.gameId
    val userId = messageInfo.userId
    val game = getGameOrError(gameId)
    if (!game.user1SetShips) {
      handleStateOfShipPlacement(game, game.user1, userId)
    } else if (!game.user2SetShips) {
      handleStateOfShipPlacement(game, game.user2!!, userId)
    }
    else if (game.winner != null) messagingService.simpleSendEndGameMessage(game, userId)
    else if (game.turn == userId) messagingService.simpleSendTurnStart(game)
    else messagingService.simpleSendWaitingToOpponent(game)
  }

  private fun handleStateOfShipPlacement(game: Game, targetUserId: Long, currentUserId: Long) {
    // if user that has not placed ships is == current user
    // we send a message that indicates that ships have not been placed
    // else we send a wait
    if (targetUserId == currentUserId)
      messagingService.simpleSendStartGameMessage(game, currentUserId)
    else
      messagingService.simpleSendWaiting(game, currentUserId)
  }
}