package com.example.demo.service

import org.springframework.stereotype.Service

@Service
class AuthenticationService {

  private val clientId = ""

  private val verifier = clientId

  fun authenticate(idToken: String): String? {
    return "test-email@mail.com"
  }
}