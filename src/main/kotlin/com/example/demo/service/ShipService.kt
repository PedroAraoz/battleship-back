package com.example.demo.service

import com.example.demo.model.Ship
import com.example.demo.repository.ShipRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShipService(
  @Autowired private val shipRepository: ShipRepository
) {

  fun saveShip(ship: Ship): Ship {
    return shipRepository.save(ship)
  }

  fun saveShips(ships: List<Ship>): List<Ship> {
    return shipRepository.saveAll(ships)
  }
}