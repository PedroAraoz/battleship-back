package com.example.demo.repository

import com.example.demo.model.Ship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ShipRepository : JpaRepository<Ship, Long> {
}