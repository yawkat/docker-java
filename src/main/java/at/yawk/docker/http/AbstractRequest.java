package at.yawk.docker.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * One-time-use request builder. Also manages initial connect.
 *
 * @author yawkat
 */
@Slf4j
public abstract class AbstractRequest<R> {
    final HttpClient client;

    protected Connection connection;
    protected ResponsePromise<R> promise = createPromise();

    protected ResponsePromise<R> createPromise() {
        return new ResponsePromise<>();
    }

    AbstractRequest(HttpClient client) {
        this.client = client;
    }

    /**
     * Handle an exception that didn't make it into the promise, probably because it occurred after headers were
     * received.
     */
    protected void handleLateException(Throwable e) {
        log.debug("Suppressed exception", e);
    }

    /**
     * Start this request. Returns when connection has been established but no data has been received yet.
     */
    public ResponsePromise<R> send() {
        assert connection == null;
        connection = Util.uninterruptible(this::acquireConnection);
        connection.exceptionHandler = e -> {
            if (!promise.completeExceptionally(e)) {
                handleLateException(e);
            }
        };
        connection.setInitializer(c -> {
            init();
            c.append(new SimpleChannelInboundHandler<HttpObject>() {
                @Override
                protected void messageReceived(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                    handleObject(msg);
                }
            });
            HttpRequest request = getRequest();
            request.headers().add("Host", client.host);
            if (log.isTraceEnabled()) {
                log.trace("> {} {} {}", request.method(), request.uri(), request.protocolVersion());
                for (Map.Entry<CharSequence, CharSequence> header : request.headers()) {
                    log.trace("> {}: {}", header.getKey(), header.getValue());
                }
                if (request instanceof FullHttpRequest) {
                    log.trace(">");
                    String body = ((FullHttpRequest) request).content().toString(StandardCharsets.UTF_8);
                    for (String line : body.split("(\n\r|\r\n|\n)")) {
                        log.trace("> {}", line);
                    }
                }
            }
            c.channel.writeAndFlush(request, c.newWriterPromise());
        });
        return promise;
    }

    protected abstract Connection acquireConnection() throws InterruptedException;

    protected void init() {}

    protected abstract HttpRequest getRequest();

    protected void handleObject(HttpObject object) {
        if (object instanceof HttpResponse) {
            promise.response = (HttpResponse) object;
            if (log.isTraceEnabled()) {
                log.trace("< {}", promise.response.status().toString());
                for (Map.Entry<CharSequence, CharSequence> header : promise.response.headers()) {
                    log.trace("< {}: {}", header.getKey(), header.getValue());
                }
            }
        }
    }
}
