package at.yawk.docker.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.charset.StandardCharsets;

/**
 * @author yawkat
 */
public class HttpStatusException extends HttpException implements StatusProvider {
    private final HttpResponseStatus status;
    private final ByteBuf body;

    HttpStatusException(Throwable cause, HttpResponseStatus status, ByteBuf body) {
        super(buildMessage(status, body), cause);
        this.status = status;
        this.body = body;
    }

    HttpStatusException(HttpResponseStatus status, ByteBuf body) {
        this(null, status, body);
    }

    private static String buildMessage(HttpResponseStatus status, ByteBuf body) {
        StringBuilder builder = new StringBuilder().append(status.toString());
        if (body.isReadable()) {
            builder.append(" - Body: ").append(body.toString(StandardCharsets.UTF_8));
        } else {
            builder.append(" - Empty Body");
        }
        return builder.toString();
    }

    @Override
    public HttpResponseStatus status() {
        return status;
    }

    public ByteBuf body() {
        return body;
    }

    @Override
    HttpException rebuildHere() {
        return new HttpStatusException(this, status, body);
    }
}
