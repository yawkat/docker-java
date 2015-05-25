package at.yawk.docker;

import at.yawk.docker.http.JsonMessageDecoder;
import at.yawk.docker.http.MessageResponseDecoder;
import at.yawk.docker.http.SimpleRequest;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

/**
 * @author yawkat
 */
abstract class JsonQuery<R> extends AbstractQuery<R> {
    private final JavaType type;

    JsonQuery(DockerClient client, Class<R> type) {
        this(client, SimpleType.construct(type));
    }

    JsonQuery(DockerClient client, JavaType type) {
        super(client);
        this.type = type;
    }

    protected void decorate(SimpleRequest<Object, R> request) {
        request.bodyEncoder(client.jsonEncoder)
                .responseDecoder(new MessageResponseDecoder<>(JsonMessageDecoder.create(client.objectMapper, type)));
    }
}
