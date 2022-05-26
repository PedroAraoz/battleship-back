package com.example.demo.service

import com.example.demo.model.User
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
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

  fun authenticate(idToken: String): String? {
    val googleIdToken: GoogleIdToken? = verifier.verify(idToken)
    return googleIdToken?.payload?.email
  }

  fun getUserFromData(idToken: String): User {
    val googleIdToken: GoogleIdToken = verifier.verify(idToken)
    val payload = googleIdToken.payload
    return User(
      id = -1,
      firstName = payload["given_name"] as String,
      lastname = payload["family_name"] as String,
      email = payload.email,
      imageUrl = payload["picture"] as String,
    )
  }

//  fun authenticate(idToken: String): String? {
//    return idToken
//  }
}