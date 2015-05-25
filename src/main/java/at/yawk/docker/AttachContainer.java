package at.yawk.docker;

import at.yawk.docker.http.StreamingRequest;
import at.yawk.docker.model.AttachLine;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author yawkat
 */
@Accessors(fluent = true)
@Setter
public class AttachContainer extends StreamQuery<String, AttachLine> {
    private String id;
    private Boolean logs;
    private Boolean stream;
    private Boolean stdin;
    private Boolean stdout;
    private Boolean stderr;
    private boolean tty;

    AttachContainer(DockerClient client) {
        super(client);
    }

    @Override
    protected void decorate(StreamingRequest<String, AttachLine> request) {
        request.bodyDecoder(tty ? new TtyDecoder() : new StreamDecoder());
        request.bodyEncoder(new MessageToByteEncoder<String>() {
            @Override
            protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
                out.writeBytes(msg.getBytes(StandardCharsets.UTF_8));
            }
        });

        request.post()
                .header("Upgrade", "tcp")
                .pathBuilder("containers", id, "attach")
                .query("stderr", AbstractQuery.flagToString(stderr))
                .query("stdout", AbstractQuery.flagToString(stdout))
                .query("stdin", AbstractQuery.flagToString(stdin))
                .query("stream", AbstractQuery.flagToString(stream))
                .query("logs", AbstractQuery.flagToString(logs));
    }

    private static class TtyDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            AttachLine attachLine = new AttachLine();
            attachLine.setStream(AttachLine.TTY);
            attachLine.setLine(in.toString(StandardCharsets.UTF_8));
            out.add(attachLine);

            in.skipBytes(in.readableBytes());
        }
    }

    private static class StreamDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            if (in.readableBytes() < 8) {
                return;
            }
            in.markReaderIndex();
            byte stream = in.readByte();
            in.skipBytes(3);
            int len = in.order(ByteOrder.BIG_ENDIAN).readInt();
            if (in.readableBytes() < len) {
                in.resetReaderIndex();
                return;
            }
            AttachLine attachLine = new AttachLine();
            attachLine.setStream(stream);
            attachLine.setLine(in.toString(in.readerIndex(), len, StandardCharsets.UTF_8));
            out.add(attachLine);
        }
    }
}
