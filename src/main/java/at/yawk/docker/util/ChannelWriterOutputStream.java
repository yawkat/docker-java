package at.yawk.docker.util;

import at.yawk.docker.http.ChannelWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author yawkat
 */
public class ChannelWriterOutputStream extends OutputStream {
    private final ChannelWriter<ByteBuf> channelWriter;

    public ChannelWriterOutputStream(ChannelWriter<ByteBuf> channelWriter) {
        this.channelWriter = channelWriter;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        channelWriter.write(Unpooled.copiedBuffer(b, off, len));
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[]{ (byte) b });
    }

    @Override
    public void flush() throws IOException {
        channelWriter.flush();
    }

    @Override
    public void close() throws IOException {
        channelWriter.finishWrite();
    }
}
