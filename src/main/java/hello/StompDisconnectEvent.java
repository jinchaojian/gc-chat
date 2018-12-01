package hello;

import hello.controller.WebSocketController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class StompDisconnectEvent implements ApplicationListener<SessionDisconnectEvent> {
    @Autowired
    private WebSocketController webSocketController;

    public StompDisconnectEvent() {
        System.out.println("new StompConnectEvent");
    }

    private final Log logger = LogFactory.getLog(StompDisconnectEvent.class);

    @Override
    public void onApplicationEvent(SessionDisconnectEvent sessionDisconnectEvent) {
        try {
            StompHeaderAccessor sha = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());
            String wsSessionId = sessionDisconnectEvent.getUser().getName();
            // 删除被当前连接用户占用的昵称
            webSocketController.removeSessionIdFromNickMap(wsSessionId);
        } catch (Exception e) {
            logger.error("onSessionDisconnectEvent error");
        }
    }
}