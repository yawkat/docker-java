package at.yawk.docker;

import at.yawk.docker.http.StreamingRequest;
import at.yawk.docker.model.CopyResource;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author yawkat
 */
@Accessors(fluent = true)
@Setter
public class CopyContainer extends StreamQuery<Void, ByteBuf> {
    private String id;
    private CopyResource resource;

    CopyContainer(DockerClient client) {
        super(client);
    }

    @Override
    protected void decorate(StreamingRequest<Void, ByteBuf> request) {
        request.post()
                .body(client.jsonEncoder.encode(resource))
                .header("Content-Type", "application/json")
                .pathBuilder("containers", id, "copy");

        request.bodyDecoder(new ByteToMessageDecoder() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                // take care: this buffer must be released later!
                out.add(in.slice().retain());
                in.skipBytes(in.readableBytes());
            }
        });
    }
}
