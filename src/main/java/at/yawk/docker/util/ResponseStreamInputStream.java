package at.yawk.docker.util;

import at.yawk.docker.http.ReadCallback;
import at.yawk.docker.http.ResponseStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

/**
 * @author yawkat
 */
public class ResponseStreamInputStream extends InputStream {
    private final ByteBuf buf = Unpooled.buffer();
    private boolean eof;

    private ResponseStreamInputStream() {}

    private synchronized void offer(ByteBuf data) {
        buf.writeBytes(data);
        notifyAll();
    }

    private synchronized void sendClose() {
        eof = true;
        notifyAll();
    }

    @Override
    public synchronized int read() throws IOException {
        try {
            while (!buf.isReadable()) {
                if (eof) { return -1; }
                wait();
            }
        } catch (InterruptedException e) {
            throw new InterruptedIOException();
        }
        byte b = buf.readByte();
        buf.discardSomeReadBytes();
        return b;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        try {
            while (!buf.isReadable()) {
                if (eof) { return -1; }
                wait();
            }
        } catch (InterruptedException e) {
            throw new InterruptedIOException();
        }
        int n = Math.min(len, buf.readableBytes());
        buf.readBytes(b, off, n);
        buf.discardSomeReadBytes();
        return n;
    }

    public static ResponseStreamInputStream attach(ResponseStream<?, ByteBuf> responseStream) {
        ResponseStreamInputStream is = new ResponseStreamInputStream();
        responseStream.readCallback(new ReadCallback<ByteBuf>() {
            @Override
            public void read(ByteBuf data) {
                is.offer(data);
            }

            @Override
            public void close() {
                is.sendClose();
            }
        });
        return is;
    }
}
