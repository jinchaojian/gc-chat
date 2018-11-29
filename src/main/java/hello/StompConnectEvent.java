package hello;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
public class StompConnectEvent implements ApplicationListener<SessionConnectEvent> {

    public StompConnectEvent() {
        System.out.println("new StompConnectEvent");
    }

    private final Log logger = LogFactory.getLog(StompConnectEvent.class);

    @Override
    public void onApplicationEvent(SessionConnectEvent sessionConnectEvent) {
        String token = "";
        try {
            StompHeaderAccessor sha = StompHeaderAccessor.wrap(sessionConnectEvent.getMessage());
            String wsSessionId = sessionConnectEvent.getUser().getName();
            String adminId = sha.getSessionAttributes().get("userId").toString();
            token = sha.getSessionAttributes().get("token").toString();
            String connSessionId = sha.getSessionId();
            logger.info(String.format("Stomp Connect Info -> connSessionId: %s, wsSessionId: %s, adminId: %s", connSessionId, wsSessionId, adminId));
        } catch (Exception e) {
            String atoken = token;
        }
    }

}