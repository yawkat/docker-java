package at.yawk.docker.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Request that does not allow stream handling but has a fixed body and response.
 *
 * @author yawkat
 */
@Setter
@Accessors(fluent = true)
public class SimpleRequest<B, R> extends AbstractHeaderRequest<R> {
    private MessageEncoder<B> bodyEncoder;
    private ResponseDecoder<? extends R> responseDecoder;
    /**
     * Whether we can use a pooled connection. Set to false if this request can take a long time with no received data.
     */
    private boolean pooled = true;

    private SimpleRequest(HttpClient client) {
        super(client);
    }

    @Override
    protected Connection acquireConnection() throws InterruptedException {
        return pooled ? client.acquireAttached() : client.acquireDetached();
    }

    public static <B, R> SimpleRequest<B, R> create(HttpClient client) {
        return new SimpleRequest<>(client);
    }

    public SimpleRequest<B, R> body(ByteBuf body) {
        return (SimpleRequest<B, R>) super.body(body);
    }

    public SimpleRequest<B, R> body(B body) {
        return body(bodyEncoder.encode(body));
    }

    public SimpleRequest<B, R> responseDecoder(ResponseDecoder<? extends R> responseDecoder) {
        this.responseDecoder = responseDecoder;
        return this;
    }

    @Override
    public SimpleRequest<B, R> method(HttpMethod method) {
        return (SimpleRequest<B, R>) super.method(method);
    }

    @Override
    public SimpleRequest<B, R> get() {
        return (SimpleRequest<B, R>) super.get();
    }

    @Override
    public SimpleRequest<B, R> post() {
        return (SimpleRequest<B, R>) super.post();
    }

    @Override
    public SimpleRequest<B, R> delete() {
        return (SimpleRequest<B, R>) super.delete();
    }

    @Override
    public SimpleRequest<B, R> patch() {
        return (SimpleRequest<B, R>) super.patch();
    }

    @Override
    public SimpleRequest<B, R> header(String key, Object value) {
        return (SimpleRequest<B, R>) super.header(key, value);
    }

    @Override
    public SimpleRequest<B, R> path(String path) {
        return (SimpleRequest<B, R>) super.path(path);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void init() {
        if (responseDecoder == null) {
            // expect an empty 2xx response
            responseDecoder = (ResponseDecoder) new MessageResponseDecoder<>(EmptyMessageDecoder.getInstance());
        }
        connection.append(new HttpObjectAggregator(1024 * 1024 * 1024)); // 1MiB max
    }

    @Override
    protected void handleObject(HttpObject object) {
        // should be aggregated
        assert object instanceof FullHttpResponse;

        super.handleObject(object);
        responseDecoder.decode(promise, ((FullHttpMessage) object).content());
        connection.close();
    }
}
