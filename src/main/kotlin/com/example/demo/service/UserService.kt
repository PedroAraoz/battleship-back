package com.example.demo.service

import com.example.demo.model.User
import com.example.demo.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody

@Service
class UserService(
  @Autowired private val userRepository: UserRepository
) {
  fun getUserById(id: Long): User? {
    return userRepository.findByIdOrNull(id)
  }

  fun saveUser(@RequestBody user: User) : User {
    return userRepository.save(user)
  }

  fun getUserByEmail(email: String): User? {
    return userRepository.getByEmail(email)
  }
}