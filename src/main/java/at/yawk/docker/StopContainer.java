package at.yawk.docker;

import at.yawk.docker.http.SimpleRequest;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author yawkat
 */
@Accessors(fluent = true)
@Setter
public class StopContainer extends AbstractQuery<Void> {
    String id;
    Integer timeout;

    StopContainer(DockerClient client) {
        super(client);
    }

    @Override
    protected void decorate(SimpleRequest<Object, Void> request) {
        request.post().pathBuilder("containers", id, "stop").query("t", timeout);
    }
}
