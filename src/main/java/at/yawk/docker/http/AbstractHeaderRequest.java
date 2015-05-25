package at.yawk.docker.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * AbstractRequest extension that provides utility methods for specifying the http headers.
 *
 * @author yawkat
 */
@Setter
@Accessors(fluent = true)
public abstract class AbstractHeaderRequest<R> extends AbstractRequest<R> {
    HttpMethod method = HttpMethod.GET;
    private Supplier<String> path = () -> "/";
    @Setter(AccessLevel.NONE) Map<String, String> headers = new LinkedHashMap<>();
    ByteBuf body = null;

    AbstractHeaderRequest(HttpClient client) {
        super(client);
    }

    public AbstractHeaderRequest<R> get() {
        return method(HttpMethod.GET);
    }

    public AbstractHeaderRequest<R> post() {
        return method(HttpMethod.POST);
    }

    public AbstractHeaderRequest<R> delete() {
        return method(HttpMethod.DELETE);
    }

    public AbstractHeaderRequest<R> patch() {
        return method(HttpMethod.PATCH);
    }

    public AbstractHeaderRequest<R> header(String key, Object value) {
        headers.put(key, String.valueOf(value));
        return this;
    }

    public AbstractHeaderRequest<R> path(String path) {
        this.path = () -> path;
        return this;
    }

    public AbstractHeaderRequest<R> body(ByteBuf body) {
        this.body = body;
        return this;
    }

    public PathBuilder pathBuilder(String item) {
        return pathBuilder().append(item);
    }

    public PathBuilder pathBuilder(String... items) {
        return pathBuilder().append(items);
    }

    public PathBuilder pathBuilder() {
        PathBuilder builder = new PathBuilder();
        this.path = builder::complete;
        return builder;
    }

    protected final String buildPath() {
        String built = path.get();
        return client.urlRewrite.rewrite(built);
    }

    @Override
    protected HttpRequest getRequest() {
        if (body == null) {
            body = Unpooled.EMPTY_BUFFER;
        } else {
            header("Content-Length", body.readableBytes());
        }
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, buildPath(), body);
        headers.forEach(request.headers()::add);
        return request;
    }
}
