package live.xsg.rpc.exchange;

import live.xsg.rpc.exception.RpcException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 结果返回的 future
 * Created by xsg on 2020/10/1.
 */
public class ResponseFuture {
    //每个请求对应的 Future 结果
    private static final Map<Long, ResponseFuture> FUTURES = new ConcurrentHashMap<>();

    private final long messageId;
    private volatile Response response;

    private long timeOut = 50000L;  // 5s 超时
    private Lock LOCK = new ReentrantLock();
    private Condition condition = LOCK.newCondition();

    public ResponseFuture(Long messageId) {
        this.messageId = messageId;
        FUTURES.put(messageId, this);
    }

    /**
     * 接收到响应结果
     * @param response 响应结果
     */
    public void received(Response response) {
        LOCK.lock();
        try {
            this.response = response;
            this.condition.signalAll();
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 获取响应结果
     * @return 响应结果
     */
    public Object get() {
        if (!this.isDone()) {
            long startMill = System.currentTimeMillis();
            LOCK.lock();
            try {
                while (!isDone()) {
                    try {
                        condition.await(timeOut, TimeUnit.MICROSECONDS);
                        if (isDone() || System.currentTimeMillis() - startMill > timeOut) break;

                    } catch (InterruptedException ignored) {}
                }
            } finally {
                LOCK.unlock();
            }
        }
        if (!isDone()) {
            FUTURES.remove(this.messageId);
            throw new RpcException("rpc timeout.");
        }
        return this.response.getResult();
    }

    /**
     * 判断远程调用结果是否已经返回
     * @return true or false
     */
    public boolean isDone() {
        return this.response != null;
    }

    /**
     * 根据 messageId 获取请求的 ResponseFuture 对象
     * @param messageId 请求消息id
     * @return ResponseFuture
     */
    public static ResponseFuture get(Long messageId) {
        return FUTURES.remove(messageId);
    }

    /**
     * messageId 与 请求的 future 关联起来
     * @param messageId 消息id
     * @param future 请求的 future 对象
     */
    public static void put(Long messageId, ResponseFuture future) {
        FUTURES.put(messageId, future);
    }
}
