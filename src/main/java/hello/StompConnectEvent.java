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
        try {
            StompHeaderAccessor sha = StompHeaderAccessor.wrap(sessionConnectEvent.getMessage());
            String wsSessionId = sessionConnectEvent.getUser().getName();
            String connSessionId = sha.getSessionId();
            logger.info(String.format("Stomp Connect Info -> connSessionId: %s, wsSessionId: %s", connSessionId, wsSessionId));
        } catch (Exception e) {
            logger.error("OnSessionConnectEvent error, sessionId: " + sessionConnectEvent.getUser().getName(), e);
        }
    }

}