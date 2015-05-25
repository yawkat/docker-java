package at.yawk.docker.http;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.type.SimpleType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

/**
 * @author yawkat
 */
public class JsonMessageDecoder<R> implements MessageDecoder<R> {
    private final ObjectReader reader;

    private JsonMessageDecoder(ObjectReader reader) {
        this.reader = reader;
    }

    public static <R> JsonMessageDecoder<R> create(ObjectMapper mapper, JavaType type) {
        return new JsonMessageDecoder<>(mapper.reader(type));
    }

    public static <R> JsonMessageDecoder<R> create(Class<R> type) {
        return create(SharedObjectMapperHolder.OBJECT_MAPPER, SimpleType.construct(type));
    }

    @Override
    public R decode(ByteBuf body) throws Exception {
        return reader.readValue(new ByteBufInputStream(body));
    }

    static class SharedObjectMapperHolder {
        static final ObjectMapper OBJECT_MAPPER;

        static {
            OBJECT_MAPPER = new ObjectMapper();
            OBJECT_MAPPER.findAndRegisterModules();
        }
    }
}
