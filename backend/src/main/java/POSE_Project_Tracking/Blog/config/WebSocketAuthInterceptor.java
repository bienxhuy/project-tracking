package POSE_Project_Tracking.Blog.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * WebSocket Authentication Interceptor
 * Extracts JWT token from WebSocket connection and sets user authentication
 * This is critical for routing messages to correct user sessions
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extract Authorization header
            List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");
            
            if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                String authHeader = authorizationHeaders.get(0);
                
                // Extract token from "Bearer <token>"
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    
                    try {
                        // Decode and validate JWT token
                        Jwt jwt = jwtDecoder.decode(token);
                        
                        // Extract userId from token (stored in "sub" claim)
                        String userId = jwt.getSubject();
                        
                        if (userId != null) {
                            // Create authentication with userId as principal
                            // IMPORTANT: The principal name MUST match the userId used in sendToUser()
                            Authentication authentication = new UsernamePasswordAuthenticationToken(
                                userId, 
                                null, 
                                Collections.singletonList(new SimpleGrantedAuthority("USER"))
                            );
                            
                            // Set user in WebSocket session
                            // This maps the WebSocket session to the userId
                            accessor.setUser(authentication);
                            
                            log.info("✅ WebSocket authenticated for user: {}", userId);
                        } else {
                            log.warn("⚠️ No userId (subject) in JWT token");
                        }
                    } catch (org.springframework.security.oauth2.jwt.JwtValidationException e) {
                        // Token expired or invalid - log as warning, not error
                        log.warn("⚠️ JWT validation failed (expired/invalid): {} - User should refresh token", 
                            e.getMessage().split(":")[0]);
                    } catch (Exception e) {
                        log.error("❌ Error authenticating WebSocket connection: {}", e.getMessage());
                    }
                }
            } else {
                log.warn("⚠️ No Authorization header in WebSocket connection");
            }
        }

        return message;
    }
}
