package at.yawk.docker;

import at.yawk.docker.http.SimpleRequest;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author yawkat
 */
@Accessors(fluent = true)
@Setter
public class DeleteContainer extends AbstractQuery<Void> {
    String id;
    Boolean volumes;
    Boolean force;

    DeleteContainer(DockerClient client) {
        super(client);
    }

    @Override
    protected void decorate(SimpleRequest<Object, Void> request) {
        request.delete().pathBuilder("containers", id, "delete")
                .query("v", flagToString(volumes))
                .query("force", flagToString(force));
    }
}
