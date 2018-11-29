package hello.model;

/**
 * 登录返回信息
 */
public enum LoginReplyEnum {
    ALREADY_LOGIN("已经登录"),
    NICK_CAN_NOT_BE_EMPTY("昵称不能为空"),
    NEED_TO_LOGIN_TO_SEND_MESSAGES("需要登录以发言"),
    NICK_ALREADY_IN_USE("昵称已占用"),
    LOGIN_SUCCESSFULLY("成功登录");
    private String content;

    LoginReplyEnum(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
