package com.example.demo.controller

import com.example.demo.model.*
import com.example.demo.service.AuthenticationService
import com.example.demo.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
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
  fun login(@RequestBody loginDTO: LoginDTO): LoginResponseDTO {
    val user: User? = userService.getUserByEmail(loginDTO.email)
    // if user does not exist we create one
    if (user == null) {
      userService.saveUser(loginDTO.toUser())
    }
    return loginDTO.toLoginResponseDTO()
  }

  private fun getUser(): User? {
    val principal = SecurityContextHolder.getContext().authentication.principal
            as org.springframework.security.core.userdetails.User
    return userService.getUserByEmail(principal.username)
  }

  private fun getUserOrError(): User {
    return getUser() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
  }

}