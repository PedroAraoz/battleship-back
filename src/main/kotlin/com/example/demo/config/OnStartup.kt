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
      email = "A@mail.com",
      firstName = "A",
      lastname = "A",
      imageUrl = "urlA"
    ))

    userService.saveUser(User(
      id = -1,
      email = "B@mail.com",
      firstName = "B",
      lastname = "B",
      imageUrl = "urlB"
    ))

    // todo hacer aboslutamete todo dentro de el websocket
    // entonces el usario puede mandar preguntas por ahi y el server
    // tiene handlers para manejar todos los posibles mensajes y le responde acorde
    // por ejemplo el usario manda GameLoad y el server devuelve el estado actual
    // del juego relevante al usario que lo pide

    // hacer 1 canal por usario por juego
  }
}


