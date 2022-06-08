package com.example.demo.model

import com.example.demo.model.GameMessageType.*
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ObjectNode
import java.awt.Point
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

enum class GameMessageType {
  START, SHIP_PLACEMENT, TURN_START, SHOT, SHOT_RESULT, WAITING,
  GET_BOARD, BOARD_DATA,
  GET_STATE,
  WINNER,
}

@JsonDeserialize(using = GameMessageDeserializer::class)
@Entity
abstract class GameMessage(
  @GeneratedValue @Id
  private val id: Long = 0,
  @JsonProperty(value = "type")
  val type: GameMessageType = START
)


@JsonDeserialize(`as` = SimpleMessage::class)
class SimpleMessage(
  type: GameMessageType = START
) : GameMessage(type = type)

@JsonDeserialize(`as` = WinnerMessage::class)
data class WinnerMessage(
  val winner: Long = 0
) : GameMessage(type = WINNER)

@JsonDeserialize(`as` = ShotMessage::class)
data class ShotMessage(
  val pos: Point? = null,
  val random: Boolean = false,
) : GameMessage(type = SHOT)

@JsonDeserialize(`as` = ShotResultMessage::class)
data class ShotResultMessage(
  val pos: Point,
  val userId: Long = 0,
  val hit: Boolean = false,
) : GameMessage(type = SHOT_RESULT) {
  constructor(shot: Shot) :
    this(pos = shot.pos, userId = shot.userId, hit = shot.hit!!)
}

@JsonDeserialize(`as` = ShipPlacementMessage::class)
data class ShipPlacementMessage(
  val ships: List<Ship> = listOf(),
  val random: Boolean = false
) : GameMessage(type = SHIP_PLACEMENT)

@JsonDeserialize(`as` = BoardDataMessage::class)
data class BoardDataMessage(
  val ships: List<Ship> = listOf(),
  val yourShots: List<Shot> = listOf(),
  val opponentShots: List<Shot> = listOf()
) : GameMessage(type = BOARD_DATA)

internal class GameMessageDeserializer : JsonDeserializer<GameMessage?>() {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): GameMessage? {
    val mapper: ObjectMapper = p.codec as ObjectMapper
    val root: ObjectNode = mapper.readTree(p)

    return mapper.readValue(root.toString(),
      when (valueOf(root.get("type").asText())) {
        START, TURN_START, GET_BOARD, WAITING, GET_STATE -> SimpleMessage::class.java
        SHIP_PLACEMENT -> ShipPlacementMessage::class.java
        SHOT -> ShotMessage::class.java
        SHOT_RESULT -> ShotResultMessage::class.java
        BOARD_DATA -> BoardDataMessage::class.java
        WINNER -> WinnerMessage::class.java
      }
    )
  }
}