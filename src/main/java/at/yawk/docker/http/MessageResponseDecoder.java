package at.yawk.docker.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Default ResponseDecoder that does basic HTTP status code handling and decodes incoming data.
 *
 * @author yawkat
 */
public class MessageResponseDecoder<R> implements ResponseDecoder<R> {
    private final MessageDecoder<R> decoder;

    public MessageResponseDecoder(MessageDecoder<R> decoder) {
        this.decoder = decoder;
    }

    protected void handleErrorStatus(ResponsePromise<? super R> promise, ByteBuf body) throws Exception {
        ByteBuf bodyCopy = body != null && body.isReadable() ? Unpooled.copiedBuffer(body) : Unpooled.EMPTY_BUFFER;
        promise.setFailure(new HttpStatusException(promise.status(), bodyCopy));
    }

    @Override
    public void decode(ResponsePromise<? super R> promise, ByteBuf body) {
        try {
            if (promise.statusCode() >= 100 && promise.statusCode() < 300) {
                promise.setSuccess(decoder.decode(body));
            } else {
                handleErrorStatus(promise, body);
            }
        } catch (Exception e) {
            promise.setFailure(e);
        }
    }
}
