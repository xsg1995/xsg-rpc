package live.xsg.rpc.exception;

/**
 * 远程调用异常
 * Created by xsg on 2020/10/1.
 */
public class RpcException extends RuntimeException {
    public RpcException() {
        super();
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }
}
