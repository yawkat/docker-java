package at.yawk.docker.http;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import lombok.SneakyThrows;

/**
 * @author yawkat
 */
class NettyCompat {
    private static MethodHandle findVirtual(Class<?> on, String name1, String name2, MethodType type) {
        try {
            try {
                return MethodHandles.publicLookup().findVirtual(on, name1, type);
            } catch (NoSuchMethodException e) {
                try {
                    return MethodHandles.publicLookup().findVirtual(on, name2, type);
                } catch (NoSuchMethodException f) {
                    throw new RuntimeException(f);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodHandle findVirtual(Class<?> on, String name1, String name2, Class<?> rtype) {
        return findVirtual(on, name1, name2, MethodType.methodType(rtype));
    }

    private static final MethodHandle RESPONSE_STATUS = findVirtual(
            HttpResponse.class, "status", "getStatus", HttpResponseStatus.class);

    @SneakyThrows
    static HttpResponseStatus status(HttpResponse response) {
        return (HttpResponseStatus) RESPONSE_STATUS.invokeExact(response);
    }

    private static final MethodHandle REQUEST_METHOD = findVirtual(
            HttpRequest.class, "method", "getMethod", HttpMethod.class);

    @SneakyThrows
    static HttpMethod method(HttpRequest response) {
        return (HttpMethod) REQUEST_METHOD.invokeExact(response);
    }

    private static final MethodHandle REQUEST_URI = findVirtual(
            HttpRequest.class, "uri", "getUri", String.class);

    @SneakyThrows
    static String uri(HttpRequest response) {
        return (String) REQUEST_URI.invokeExact(response);
    }

    private static final MethodHandle MESSAGE_PROTOCOL_VERSION = findVirtual(
            HttpMessage.class, "protocolVersion", "getProtocolVersion", HttpVersion.class);

    @SneakyThrows
    static HttpVersion protocolVersion(HttpMessage response) {
        return (HttpVersion) MESSAGE_PROTOCOL_VERSION.invokeExact(response);
    }

    private static final MethodHandle RESPONSE_DECODER_RESULT = findVirtual(
            HttpResponse.class, "decoderResult", "getDecoderResult", DecoderResult.class);

    @SneakyThrows
    static DecoderResult decoderResult(HttpResponse response) {
        return (DecoderResult) RESPONSE_DECODER_RESULT.invokeExact(response);
    }
}
