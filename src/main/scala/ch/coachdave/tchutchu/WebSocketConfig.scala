package ch.coachdave.tchutchu

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.{EnableWebSocketMessageBroker, StompEndpointRegistry, WebSocketMessageBrokerConfigurer}

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig extends WebSocketMessageBrokerConfigurer {

  override def configureMessageBroker(registry: MessageBrokerRegistry): Unit =
    registry.enableSimpleBroker("/topic", "/queue")
    registry.setApplicationDestinationPrefixes("/app")
    //registry.setUserDestinationPrefix("/user")

  override def registerStompEndpoints(registry: StompEndpointRegistry): Unit =
    // Native WebSocket endpoint (preferred - no deprecated unload events)
    registry.addEndpoint("/game-ws").setAllowedOriginPatterns("*")
      .setHandshakeHandler(new UserHandshakeHandler)
    // SockJS fallback endpoint for older browsers
    registry.addEndpoint("/game-ws-sockjs").setAllowedOriginPatterns("*")
      .setHandshakeHandler(new UserHandshakeHandler)
      .withSockJS()
}

