package com.example.demo.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.DefaultContentTypeResolver
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.util.MimeTypeUtils
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {
  override fun configureMessageBroker(config: MessageBrokerRegistry) {
    config.enableSimpleBroker("/queue")
    config.setApplicationDestinationPrefixes("/app")
  }

  override fun registerStompEndpoints(registry: StompEndpointRegistry) {
    registry.addEndpoint("/ws")
      .setAllowedOriginPatterns(
        "http://localhost:3000",
      ).withSockJS()
      .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1.5.1/dist/sockjs.min.js")
  }

  override fun configureMessageConverters(messageConverters: MutableList<MessageConverter?>): Boolean {
    val resolver = DefaultContentTypeResolver()
    resolver.defaultMimeType = MimeTypeUtils.APPLICATION_JSON
    val converter = MappingJackson2MessageConverter()
    converter.objectMapper = ObjectMapper()
    converter.contentTypeResolver = resolver
    messageConverters.add(converter)
    return false
  }
}