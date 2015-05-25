package at.yawk.docker.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPromise;
import java.io.Closeable;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yawkat
 */
@Slf4j
class Connection implements Closeable {
    private final HttpClient pool;
    final Channel channel;
    Consumer<Throwable> exceptionHandler;

    ChannelHandler lastHttpHandler = null;
    volatile boolean attached;

    Connection(HttpClient pool, Channel channel) {
        this.pool = pool;
        this.channel = channel;
    }

    void handleException(Throwable cause) {
        cause.printStackTrace();
        if (exceptionHandler != null) {
            exceptionHandler.accept(cause);
        } else {
            log.warn("Uncaught exception", cause);
        }
        disconnect();
    }

    void setInitializer(ConnectionInitializer initializer) {
        // already initialized, run immediately
        initializer.initialize(this);
    }

    public Connection clearHandlers() {
        while (channel.pipeline().last() != lastHttpHandler) {
            channel.pipeline().removeLast();
        }
        return this;
    }

    public Connection append(ChannelHandler handler) {
        channel.pipeline().addLast(handler);
        return this;
    }

    public void release() {
        if (attached) {
            if (channel.isActive()) {
                clearHandlers();
                pool.channelPool.release(channel);
            } else {
                channel.close();
            }
        } else {
            disconnect();
        }
    }

    /**
     * Forces a disconnect on this channel. This channel will not be given back to the connection pool on
     * release/close.
     */
    public void disconnect() {
        attached = false;
        channel.disconnect();
    }

    /**
     * This close method releases the connection, possibly giving it back to the connection pool.
     */
    @Override
    public void close() {
        release();
    }

    ChannelPromise newWriterPromise() {
        ChannelPromise promise = channel.newPromise();
        promise.addListener(future -> {
            Throwable error = promise.cause();
            if (error != null) {
                Connection.this.handleException(error);
            }
        });
        return promise;
    }
}
