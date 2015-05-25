package at.yawk.docker.http;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Provider for HTTP status info.
 *
 * @author yawkat
 */
public interface StatusProvider {
    HttpResponseStatus status();

    default int statusCode() {
        return status().code();
    }

    default String statusMessage() {
        return status().reasonPhrase().toString();
    }
}
