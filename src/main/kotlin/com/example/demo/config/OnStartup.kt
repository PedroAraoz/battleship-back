package com.example.demo.config

import com.example.demo.model.User
import com.example.demo.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class OnStartup(
  @Autowired private val userService: UserService,
//  @Autowired private val gameService: GameService,
//  @Autowired private val controller: GameSocketController
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
//    val id = gameService.joinOrCreateGame(a)
//    val uuid = UUID.fromString(id)
//    gameService.joinOrCreateGame(b)
//    gameService.startGame(uuid)
//    val placement1 = ShipPlacementMessage(
//      listOf(
//        Ship(size = 2, startPos = Point(0, 0), endPos = Point(0, 1)),
//        Ship(size = 2, startPos = Point(1, 0), endPos = Point(1, 1))
//      )
//    )
//
//    val placement2 = ShipPlacementMessage(
//      listOf(
//        Ship(size = 2, startPos = Point(0, 0), endPos = Point(0, 1)),
//        Ship(size = 2, startPos = Point(1, 0), endPos = Point(1, 1))
//      )
//    )
//    controller.handleMessage(uuid, a.id, placement1)
//
//    controller.handleMessage(uuid, a.id, SimpleMessage(GameMessageType.GET_STATE))
//
//    controller.handleMessage(uuid, b.id, SimpleMessage(GameMessageType.GET_STATE))
//
//    controller.handleMessage(uuid, b.id, placement2)
//
//    controller.handleMessage(uuid, a.id, ShotMessage(Point(0,0)))
//    controller.handleMessage(uuid, b.id, ShotMessage(Point(5,5)))
//
//    controller.handleMessage(uuid, a.id, ShotMessage(Point(0,1)))
//
//    controller.handleMessage(uuid, b.id, SimpleMessage(GameMessageType.GET_BOARD))
//
//    controller.handleMessage(uuid, b.id, SimpleMessage(GameMessageType.GET_STATE))
//
//    controller.handleMessage(uuid, b.id, ShotMessage(Point(5,6)))
//
//    controller.handleMessage(uuid, b.id, SimpleMessage(GameMessageType.GET_STATE))
//
//
//    controller.handleMessage(uuid, a.id, ShotMessage(Point(1,0)))
//    controller.handleMessage(uuid, b.id, ShotMessage(Point(7,7)))
//
//
//    controller.handleMessage(uuid, a.id, ShotMessage(Point(1,1)))
//
//    controller.handleMessage(uuid, a.id, SimpleMessage(GameMessageType.GET_STATE))
//
//
//    val updatedGame = gameService.getGameOrError(uuid)
//    print("finished game")
  }
}


