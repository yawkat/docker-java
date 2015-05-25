package at.yawk.docker.http;

/**
 * Callback when a message is received and when the reader is closed.
 *
 * @author yawkat
 */
public interface ReadCallback<T> extends AutoCloseable {
    void read(T item);

    default void exceptionCaught(Throwable exception) {}

    ;

    @Override
    default void close() {}
}
