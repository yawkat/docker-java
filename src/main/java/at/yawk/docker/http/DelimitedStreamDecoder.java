package at.yawk.docker.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * ByteToMessageDecoder that splits the input into chunks delimited by a given byte (or \n by default) and then decodes
 * each chunk with a given decoder.
 *
 * @author yawkat
 */
public class DelimitedStreamDecoder<R> extends ByteToMessageDecoder {
    private final MessageDecoder<R> decoder;
    private final byte delimiter;

    public DelimitedStreamDecoder(MessageDecoder<R> decoder, byte delimiter) {
        this.decoder = decoder;
        this.delimiter = delimiter;
    }

    public DelimitedStreamDecoder(MessageDecoder<R> decoder) {
        this(decoder, (byte) '\n');
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out)
            throws Exception {
        int start = buf.readerIndex();
        int end = start + buf.readableBytes();
        int index = buf.indexOf(start, end, delimiter);
        if (index == -1) { return; }
        ByteBuf slice = buf.slice(start, index - start);
        out.add(decoder.decode(slice));
        buf.readerIndex(index + 1);
    }
}
