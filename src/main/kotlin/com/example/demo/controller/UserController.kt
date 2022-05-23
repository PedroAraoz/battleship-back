package com.example.demo.controller

import com.example.demo.model.LoginDTO
import com.example.demo.model.User
import com.example.demo.model.toUser
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
  fun login(@RequestBody loginDTO: LoginDTO) {
    val user: User? = authenticate(loginDTO.idToken)
    if (user == null) {
      userService.saveUser(loginDTO.toUser())
    }
    //login
  }

  private fun authenticate(idToken: String): User? {
    val email: String = authenticationService.authenticate(idToken) ?: throw ResponseStatusException(
      HttpStatus.UNAUTHORIZED,
      "Error authenticating google idToken"
    )
    return userService.getUserByEmail(email)
  }

}