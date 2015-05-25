package at.yawk.docker.http;

import io.netty.buffer.ByteBuf;

/**
 * bytes -> msg
 *
 * @author yawkat
 */
public interface MessageDecoder<B> {
    B decode(ByteBuf message) throws Exception;
}
