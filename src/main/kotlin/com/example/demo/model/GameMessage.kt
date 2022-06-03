package com.example.demo.model

import com.example.demo.model.GameMessageType.*
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ObjectNode


enum class GameMessageType {
  START, GET_BOARD, POSITION_SHIP,
  WINNER,
  SHIP_POSITION
}

@JsonDeserialize(using = GameMessageDeserializer::class)
abstract class GameMessage {
  @JsonProperty(value = "type")
  val type: GameMessageType = START
}

@JsonDeserialize(`as` = SimpleMessage::class)
class SimpleMessage : GameMessage()

@JsonDeserialize(`as` = WinnerMessage::class)
data class WinnerMessage(
  val winner: String = ""
) : GameMessage()

@JsonDeserialize(`as` = ShipPositionMessage::class)
data class ShipPositionMessage(
  val list: List<ShipPos> = listOf()
) : GameMessage()


data class ShipPos(
  val ship: String = "",
  val x: Number = 0,
  val y: Number = 0,
)

internal class GameMessageDeserializer : JsonDeserializer<GameMessage?>() {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): GameMessage? {
    val mapper: ObjectMapper = p.codec as ObjectMapper
    val root: ObjectNode = mapper.readTree(p)

    return mapper.readValue(root.toString(),
      when (valueOf(root.get("type").asText())) {
        START,
        GET_BOARD,
        POSITION_SHIP -> SimpleMessage::class.java
        SHIP_POSITION -> ShipPositionMessage::class.java
        WINNER -> WinnerMessage::class.java
      }

    )

  }
}