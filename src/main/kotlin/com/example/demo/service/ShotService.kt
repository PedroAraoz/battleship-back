package com.example.demo.service

import com.example.demo.model.Shot
import com.example.demo.repository.ShotRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShotService(
  @Autowired private val shotRepository: ShotRepository
) {

  fun saveShot(shot: Shot): Shot {
    return shotRepository.save(shot)
  }
}