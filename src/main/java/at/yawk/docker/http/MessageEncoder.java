package at.yawk.docker.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * msg -> bytes
 *
 * @author yawkat
 */
public interface MessageEncoder<B> {
    default ByteBuf encode(B message) {
        ByteBuf buf = Unpooled.buffer();
        encode(message, buf);
        return buf;
    }

    void encode(B message, ByteBuf target);
}
