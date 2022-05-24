package com.example.demo.config

import com.example.demo.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class AuthFilterConfigurer(
  @Autowired private val authenticationService: AuthenticationService
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {
  override fun configure(builder: HttpSecurity) {
    val filter = AuthFilter(authenticationService)
    builder.addFilterBefore(filter, UsernamePasswordAuthenticationFilter::class.java)
  }
}