package com.example.smarthome.simulation;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configures WebSocket messaging for the smart home simulation system.
 *
 * Enables STOMP-based communication between the backend and frontend,
 * including a simple message broker for broadcasting updates to clients
 * subscribed to "/topic" destinations.
 *
 * Also registers the "/ws" endpoint for WebSocket connections with SockJS fallback support.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures message broker for handling application and topic-based messaging.
     *
     * "/app" is used for messages sent from clients to the server.
     * "/topic" is used for broadcasting messages from server to clients.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the WebSocket endpoint used by clients to connect to the server.
     *
     * Enables SockJS fallback and allows cross-origin connections.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
