package com.example.demo.config

import com.example.demo.model.User
import com.example.demo.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class OnStartup (
  @Autowired private val userService: UserService
  ) {
  @EventListener(ApplicationReadyEvent::class)
  fun doSomethingAfterStartup() {
    userService.saveUser(User(
      id = -1,
      email = "pedro@mail.com",
      firstName = "pedro",
      lastname = "a",
      imageUrl = "url"
    ))
  }
}


