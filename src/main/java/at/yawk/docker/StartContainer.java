package at.yawk.docker;

import at.yawk.docker.http.SimpleRequest;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author yawkat
 */
@Accessors(fluent = true)
@Setter
public class StartContainer extends AbstractQuery<Void> {
    String id;

    StartContainer(DockerClient client) {
        super(client);
    }

    @Override
    protected void decorate(SimpleRequest<Object, Void> request) {
        request.post().pathBuilder("containers", id, "start");
    }
}
