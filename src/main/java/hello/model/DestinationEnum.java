package hello.model;

/**
 * 固定 "频道"
 */
public enum DestinationEnum {
    QUEUE_LOGIN("/queue/login", "登录"),
    ;
    private String destination;
    private String comment;

    DestinationEnum(String destination, String comment) {
        this.destination = destination;
        this.comment = comment;
    }

    public String getDestination() {
        return destination;
    }
}
