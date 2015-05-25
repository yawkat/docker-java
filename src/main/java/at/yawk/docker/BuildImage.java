package at.yawk.docker;

import at.yawk.docker.http.DelimitedStreamDecoder;
import at.yawk.docker.http.JsonMessageDecoder;
import at.yawk.docker.http.StreamingRequest;
import at.yawk.docker.model.ImageCreationProgress;
import com.fasterxml.jackson.databind.type.SimpleType;
import io.netty.buffer.ByteBuf;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author yawkat
 */
@Accessors(fluent = true)
@Setter
public class BuildImage extends StreamQuery<ByteBuf, ImageCreationProgress> {
    private String dockerfile;
    private String name;
    private String remote;
    private Boolean noVerbose;
    private Boolean noCache;
    private Boolean pull;
    private Boolean removeIntermediateOnSuccess;
    private Boolean alwaysRemoveIntermediate;

    BuildImage(DockerClient client) {
        super(client);
    }

    @Override
    protected void decorate(StreamingRequest<ByteBuf, ImageCreationProgress> request) {
        request.post().chunked(true)
                .header("Content-Type", "application/tar")
                .pathBuilder("build")
                .query("dockerfile", dockerfile)
                .query("t", name)
                .query("remote", remote)
                .query("q", AbstractQuery.flagToString(noVerbose))
                .query("nocache", AbstractQuery.flagToString(noCache))
                .query("pull", AbstractQuery.flagToString(pull))
                .query("rm", AbstractQuery.flagToString(removeIntermediateOnSuccess))
                .query("forcerm", AbstractQuery.flagToString(alwaysRemoveIntermediate));

        // need no encoder since we send ByteBufs

        // progress decoder
        JsonMessageDecoder<ImageCreationProgress> decoder =
                JsonMessageDecoder.<ImageCreationProgress>create(client.objectMapper,
                                                                 SimpleType.construct(ImageCreationProgress.class));
        request.bodyDecoder(new DelimitedStreamDecoder<>(decoder));
    }
}
