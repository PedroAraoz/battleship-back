package com.example.demo.service

import com.example.demo.helper.unwrap
import com.example.demo.model.Game
import com.example.demo.model.Message
import com.example.demo.model.User
import com.example.demo.repository.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class GameService(
  @Autowired private val gameRepository: GameRepository
) {
  fun addMessage(game: Game, message: Message) {
    game.addMessage(message)
    gameRepository.save(game)
  }

  fun findById(id: UUID): Game? {
    return gameRepository.findById(id).unwrap()
  }

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

  fun ifUserBelongs(userId: Long, game: Game): Boolean {
    return game.user1 == userId || game.user2 == userId
  }
}