package com.example.demo.config

import com.example.demo.controller.GameSocketController.MessageInfo
import com.example.demo.model.Ship
import com.example.demo.model.ShipPlacementMessage
import com.example.demo.model.User
import com.example.demo.service.GameService
import com.example.demo.service.ShipService
import com.example.demo.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.awt.Point
import java.util.*

@Component
class OnStartup(
  @Autowired private val userService: UserService,
  @Autowired private val shipService: ShipService,
  @Autowired private val gameService: GameService,
) {
  @EventListener(ApplicationReadyEvent::class)
  fun doSomethingAfterStartup() {
    val a = userService.saveUser(User(
      id = -1,
      email = "A@mail.com",
      firstName = "A",
      lastname = "A",
      imageUrl = "urlA"
    ))
    val b = userService.saveUser(User(
      id = -1,
      email = "B@mail.com",
      firstName = "B",
      lastname = "B",
      imageUrl = "urlB"
    ))

    val id = gameService.joinOrCreateGame(a)
    val uuid = UUID.fromString(id)
    gameService.joinOrCreateGame(b)
    gameService.startGame(uuid)
    val game = gameService.getGame(uuid)!!

    val validShips = listOf(5, 4, 3, 3, 2).sorted()

    val ships = listOf(
      Ship(size = 5, startPos = Point(0,0), endPos = Point(0,4)),
      Ship(size = 4, startPos = Point(1,0), endPos = Point(1,4)),
      Ship(size = 3, startPos = Point(2,0), endPos = Point(2,2)),
      Ship(size = 3, startPos = Point(3,0), endPos = Point(3,2)),
      Ship(size = 2, startPos = Point(4,0), endPos = Point(4,1)),
    )

    gameService.handleShipPlacement(
      ShipPlacementMessage(
        ships, false
      ),
      MessageInfo(game.id, a.id)
    )
    gameService.handleShipPlacement(
      ShipPlacementMessage(
        ships, false
      ),
      MessageInfo(game.id, b.id)
    )
    val finalGame = gameService.getGame(uuid)
    print("asd")
  }
}


