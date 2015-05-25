package at.yawk.docker.http;

/**
 * Access to a writable channel that accepts messages of type S.
 *
 * If S is ByteBuf, you can use {@link at.yawk.docker.util.ChannelWriterOutputStream} to write to this channel easily.
 *
 * @author yawkat
 */
public interface ChannelWriter<S> {
    void write(S message);

    void flush();

    void writeAndFlush(S message);

    /**
     * Mark this channels output as finished and notify the server that we have no more input.
     */
    void finishWrite();
}
