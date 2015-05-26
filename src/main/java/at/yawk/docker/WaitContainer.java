package at.yawk.docker;

import at.yawk.docker.http.SimpleRequest;
import at.yawk.docker.model.StatusCode;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author yawkat
 */
@Accessors(fluent = true)
@Setter
public class WaitContainer extends JsonQuery<StatusCode> {
    String id;

    WaitContainer(DockerClient client) {
        super(client, StatusCode.class);
    }

    @Override
    protected void decorate(SimpleRequest<Object, StatusCode> request) {
        super.decorate(request);

        request.pooled(false) // don't pool as this request blocks
                .post().pathBuilder("containers", id, "wait");

    }
}
