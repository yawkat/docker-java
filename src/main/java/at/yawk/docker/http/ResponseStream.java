package at.yawk.docker.http;

import java.io.Closeable;

/**
 * IO class that will be returned by the ResponsePromise produced by streaming requests. Created after headers are
 * received.
 *
 * @author yawkat
 */
public class ResponseStream<S, R> implements Closeable, ChannelWriter<S> {
    private final StreamingRequest<S, R> request;
    private final Connection connection;

    ResponseStream(StreamingRequest<S, R> request, Connection connection) {
        this.request = request;
        this.connection = connection;
    }

    public void readCallback(ReadCallback<R> callback) {
        request.reader.setCallback(callback);
    }

    public void awaitReaderCloseUninterruptibly() {
        Util.uninterruptible(this::awaitReaderClose);
    }

    public void awaitReaderClose() throws InterruptedException {
        request.reader.awaitClose();
    }

    public boolean isOpen() {
        return !request.reader.isClosed();
    }

    @Override
    public void close() {
        connection.close();
    }

    @Override
    public void write(S message) {
        request.write(message);
    }

    @Override
    public void flush() {
        request.flush();
    }

    @Override
    public void writeAndFlush(S message) {
        request.writeAndFlush(message);
    }

    @Override
    public void finishWrite() {
        request.finishWrite();
    }
}
