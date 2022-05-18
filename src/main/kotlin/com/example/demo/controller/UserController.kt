package com.example.demo.controller

import com.example.demo.model.User
import com.example.demo.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/user")
class UserController(
  @Autowired private val userService: UserService
) {

  @GetMapping("/{id}")
  fun getUser(@PathVariable("id") id: Long): User {
    return userService.getUserById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
  }

  @GetMapping("")
  fun getUser(@RequestParam("email", required = true) email: String): User {
    return userService.getUserByEmail(email)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
  }

  @PostMapping("/")
  fun saveUser(@RequestBody user: User) {
    userService.saveUser(user)
  }
}