package at.yawk.docker.http;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import lombok.Getter;

/**
 * @author yawkat
 */
public class EmptyMessageDecoder implements MessageDecoder<Void> {
    @Getter private static final EmptyMessageDecoder instance = new EmptyMessageDecoder();

    private EmptyMessageDecoder() {}

    @Override
    public Void decode(ByteBuf body) throws Exception {
        if (body.isReadable()) {
            throw new IOException("Expected empty body but received " + body.readableBytes() + " bytes");
        }
        return null;
    }
}
