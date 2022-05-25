package com.example.demo.service

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.stereotype.Service

@Service
class AuthenticationService {

  private val clientIds = listOf("64992691436-bfdvk9u682iut84mk1f9kvbll44u5dqt.apps.googleusercontent.com")

  private val verifier = GoogleIdTokenVerifier.Builder(
    NetHttpTransport(), GsonFactory.getDefaultInstance()
  ).setAudience(clientIds).build()

//  TODO Disabled for testing reasons
//  fun authenticate(idToken: String): String? {
//    val googleIdToken: GoogleIdToken? = verifier.verify(idToken)
//    return googleIdToken?.payload?.email
//  }

  fun authenticate(idToken: String): String? {
    return idToken
  }
}