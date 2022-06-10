package com.example.demo.controller

import com.example.demo.model.LoginDTO
import com.example.demo.model.User
import com.example.demo.service.AuthenticationService
import com.example.demo.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/user")
class UserController(
  @Autowired private val userService: UserService,
  @Autowired private val authenticationService: AuthenticationService
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

  @PostMapping("/login")
  fun login(@RequestBody loginDTO: LoginDTO): User {
    val email: String = authenticationService.authenticate(loginDTO.idToken)
      ?: throw ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "id token not valid"
      )
    val user: User? = userService.getUserByEmail(email)
    // if user does not exist we create one
    return if (user == null) {
      val newUser = authenticationService.getUserFromData(loginDTO.idToken)
      userService.saveUser(newUser)
    } else {
      user
    }
  }
}