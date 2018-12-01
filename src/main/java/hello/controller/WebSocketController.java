package hello.controller;

import hello.model.DestinationEnum;
import hello.model.LoginMessage;
import hello.model.LoginReplyEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.*;

import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bill
 */
@Controller
public class WebSocketController {
    // 客户端 PrincipalName 与 用户名之间的对应关系, 1 对 1
    private Map<String, String> principalNameUserNameMap = new ConcurrentHashMap<>();

    @Autowired
    //通过SimpMessagingTemplate模板向浏览器发送消息。如果是广播模式，可以直接使用注解@SendTo
    private SimpMessagingTemplate simpMessagingTemplate;

    //开启STOMP协议来传输基于代理的消息，这时控制器支持使用@MessageController，就像使用@RequestMapping是一样的
    //当浏览器向服务端发送请求时，通过@MessageController映射/chat这个路径
    //在SpringMVC中，可以直接在参数中获得principal,其中包含当前用户的信息
    @MessageMapping("/hello")
//    @SendTo("/queue/reply")
//    @SendToUser("/queue/reply/bill")
//    @SendTo("/topic/greetings")
    public void handleHello(Principal principal, String msg) {
//    public Greeting handleHello(Principal principal, String msg) {
        //通过SimpMessagingTemplate的convertAndSendToUser向用户发送消息。
        //第一参数表示接收信息的用户，第二个是浏览器订阅的地址，第三个是消息本身
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/queue/reply",
                principal.getName() + "-发送:" + msg);
//        return new Greeting(principal.getName() + "-发送:" + msg);
    }

    @MessageMapping("/chat")
//    @SendTo("/topic/chat")
    public void handleChat(Message<Object> message, Principal principal, String msg) {
        if (!hasLogin(principal.getName())) {
            // 未登录，需要登录以发言
            simpMessagingTemplate.convertAndSendToUser(principal.getName(), DestinationEnum.QUEUE_LOGIN.getDestination(), LoginReplyEnum.NEED_TO_LOGIN_TO_SEND_MESSAGES.getContent());
        } else {
            String reply = String.format("%s &lt;%s&gt; %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), getUserNick(principal.getName()), msg);
            simpMessagingTemplate.convertAndSend("/topic/chat", reply);
        }
    }

    @MessageMapping("/login")
    @SendToUser("/queue/login")
    public String login(Principal principal, LoginMessage loginMessage) {
        if (StringUtils.isBlank(loginMessage.getNick())) {
            // 用户名为空
            return LoginReplyEnum.NICK_CAN_NOT_BE_EMPTY.getContent();
        } else if (Objects.equals(loginMessage.getNick(), getUserNick(principal.getName()))) {
            // 已经登录，向客户端发送提醒
            return LoginReplyEnum.ALREADY_LOGIN.getContent();
        } else if (nickExists(loginMessage.getNick())) {
            // 昵称已使用，提醒用户换一个昵称
            return LoginReplyEnum.NICK_ALREADY_IN_USE.getContent();

        } else {
            // 正常登录
            principalNameUserNameMap.put(principal.getName(), loginMessage.getNick());
            return LoginReplyEnum.LOGIN_SUCCESSFULLY.getContent();
        }
    }

    /**
     * 判断昵称是否已经存在
     *
     * @param nick
     * @return
     */
    public boolean nickExists(String nick) {
        return principalNameUserNameMap.containsValue(nick);
    }

    /**
     * 根据 PrincipalName 获取用户昵称
     *
     * @param principalName
     * @return
     */
    public String getUserNick(String principalName) {
        return principalNameUserNameMap.get(principalName);
    }

    /**
     * 判断当前用户是否已登录
     *
     * @param principalName
     * @return
     */
    public boolean hasLogin(String principalName) {
        return principalNameUserNameMap.containsKey(principalName);
    }

    /**
     * 删除 SessionId 对应用户占用的 Nick
     *
     * @param sessionId
     * @return
     */
    public String removeSessionIdFromNickMap(String sessionId) {
        return principalNameUserNameMap.remove(sessionId);
    }
}