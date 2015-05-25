package at.yawk.docker.http;

import io.netty.buffer.ByteBuf;

/**
 * Class that decides what to do after response headers were received: either throw, or continue reading data. Only
 * used in non-streaming requests.
 *
 * @author yawkat
 */
public interface ResponseDecoder<R> {
    void decode(ResponsePromise<? super R> promise, ByteBuf body);
}
