package at.yawk.docker.http;

/**
 * ResponsePromise that returns a ResponseStream. Added benefit of being writable so you can write data to a streaming
 * request before this promise is completed (before server headers are received).
 *
 * @author yawkat
 */
public class StreamingResponsePromise<S, R> extends ResponsePromise<ResponseStream<S, R>> implements ChannelWriter<S> {
    private final StreamingRequest<S, R> request;

    StreamingResponsePromise(StreamingRequest<S, R> request) {
        this.request = request;
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
