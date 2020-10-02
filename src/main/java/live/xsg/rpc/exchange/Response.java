package live.xsg.rpc.exchange;

/**
 * 响应消息
 * Created by xsg on 2020/10/1.
 */
public class Response implements java.io.Serializable {
    //心跳响应码
    public static int HEARTBEAT_CODE = 1;
    //错误码
    public static int ERROR_CODE = -1;

    //响应的错误码，正常响应为0，非0为异常响应
    private int resultCode;
    //异常信息
    private String errorMsg;
    //响应结果
    private Object result;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
