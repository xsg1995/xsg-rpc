package live.xsg.rpc.exchange;

/**
 * 消息，包括 消息头 + 消息体
 * Created by xsg on 2020/10/1.
 */
public class Message<T> {
    //消息头
    private Header header;
    //消息体
    private T body;

    public Message(Header header, T body) {
        this.header = header;
        this.body = body;
    }

    public Header getHeader() {
        return header;
    }

    public T getBody() {
        return body;
    }
}
