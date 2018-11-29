package hello;

import org.springframework.context.annotation.*;
import org.springframework.http.server.*;
import org.springframework.messaging.simp.config.*;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.*;

import java.security.*;
import java.util.*;

/**
 * @author bill
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private volatile int userID = 0;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic", "/queue");
//        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic", "/user", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user/");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/sockjs")
                .setHandshakeHandler(handshakeHandler()).setAllowedOrigins("*").withSockJS()
                .setClientLibraryUrl("//cdn.jsdelivr.net/sockjs/1/sockjs.min.js")
                .setInterceptors(sessionAuthHandshakeInterceptor());
    }

    @Bean
    public SessionAuthHandshakeInterceptor sessionAuthHandshakeInterceptor() {
        return new SessionAuthHandshakeInterceptor();
    }

    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                String sessionId = String.valueOf(++userID);
                return () -> sessionId;
            }
        };
    }
}
