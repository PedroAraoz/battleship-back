package com.example.demo.controller

import com.example.demo.helper.printlnCyan
import com.example.demo.model.GameMessage
import com.example.demo.model.GameMessageType.*
import com.example.demo.model.ShipPlacementMessage
import com.example.demo.model.ShotMessage
import com.example.demo.service.GameService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller
import org.springframework.web.server.ResponseStatusException
import java.util.*


@Controller
class GameSocketController(
  @Autowired private val gameService: GameService,
) {

  data class MessageInfo(
    val gameId: UUID,
    val userId: Long
  )

  @MessageMapping("/game/{gameId}/{userId}")
  fun handleMessage(
    @DestinationVariable gameId: UUID,
    @DestinationVariable userId: Long,
    @Payload m: GameMessage
  ): Boolean {
    val messageInfo = MessageInfo(gameId, userId)
    val jsonMessage = ObjectMapper().writeValueAsString(m)
    printlnCyan("Received message: $jsonMessage")
    when (m.type) {

      SHIP_PLACEMENT -> gameService.handleShipPlacement(m as ShipPlacementMessage, messageInfo)
      SHOT -> gameService.handleShot(m as ShotMessage, messageInfo)
      GET_BOARD -> gameService.getBoard(messageInfo)
      GET_STATE -> gameService.getState(messageInfo)
      SURRENDER -> gameService.surrender(messageInfo)
      else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST)
    }
    return true
  }
}


