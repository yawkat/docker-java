package at.yawk.docker;

import at.yawk.docker.http.SimpleRequest;
import at.yawk.docker.model.ContainerInfo;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author yawkat
 */
@Accessors(fluent = true)
@Setter
public class InspectContainer extends JsonQuery<ContainerInfo> {
    private String id;

    InspectContainer(DockerClient client) {
        super(client, ContainerInfo.class);
    }

    @Override
    protected void decorate(SimpleRequest<Object, ContainerInfo> request) {
        super.decorate(request);

        request.pathBuilder("containers", id, "json");
    }
}
