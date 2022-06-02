package com.example.demo.config

import com.example.demo.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class WebSecurityConfig(
  @Autowired private val authenticationService: AuthenticationService
) : WebSecurityConfigurerAdapter() {


  override fun configure(http: HttpSecurity) {

    // Disable CSRF (cross site request forgery)
    http.csrf().disable()

    // No session will be created or used by spring security
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

    // Entry points
    http.authorizeRequests()
      .antMatchers(HttpMethod.POST, "/user/login", "/user/").permitAll()
      .antMatchers(HttpMethod.GET, "/ws/**").permitAll()
      .anyRequest().authenticated().and().cors()

    // Apply Filter
    http.apply(AuthFilterConfigurer(authenticationService))
  }
}