package at.yawk.docker.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author yawkat
 */
public class JsonMessageEncoder implements MessageEncoder<Object> {
    private final ObjectMapper objectMapper;

    private JsonMessageEncoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static JsonMessageEncoder create(ObjectMapper mapper) {
        return new JsonMessageEncoder(mapper);
    }

    public static JsonMessageEncoder create() {
        return create(JsonMessageDecoder.SharedObjectMapperHolder.OBJECT_MAPPER);
    }

    @Override
    public void encode(Object message, ByteBuf target) {
        try {
            ByteBufOutputStream stream = new ByteBufOutputStream(target);
            objectMapper.writeValue(stream, message);
            stream.write('\n');
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
