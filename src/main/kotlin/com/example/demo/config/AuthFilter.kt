package com.example.demo.config

import com.example.demo.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthFilter(
  @Autowired private val authenticationService: AuthenticationService
) : OncePerRequestFilter() {

  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    if ("/user/login" == request.requestURI){
      filterChain.doFilter(request, response)
      return
    }
    val idToken: String? = getToken(request)
    if (idToken == null) {
      // TODO fix custom error not working
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "no token")
      return
    }
    val email: String? = authToken(idToken)
    if (email == null) {
      // TODO fix custom error not working
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "invalid token")
      return
    }
    val userDetails = User.withUsername(email)
      .authorities("normal")
      .password("")
      .accountExpired(false)
      .accountLocked(false)
      .credentialsExpired(false)
      .disabled(false)
      .build()
    val auth = UsernamePasswordAuthenticationToken(
      userDetails,
      "",
      userDetails.authorities
    )
    SecurityContextHolder.getContext().authentication = auth
    filterChain.doFilter(request, response)
  }

  private fun authToken(idToken: String): String? {
    return authenticationService.authenticate(idToken)
  }

  private fun getToken(request: HttpServletRequest): String? {
    return request.cookies.find { it.name == "IDTOKEN" }?.value
  }

}
