package at.yawk.docker;

import at.yawk.docker.http.SimpleRequest;
import at.yawk.docker.model.ContainerConfig;
import at.yawk.docker.model.ContainerCreated;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author yawkat
 */
@Accessors(fluent = true)
@Setter
public class CreateContainer extends JsonQuery<ContainerCreated> {
    private ContainerConfig config;
    private String name;

    CreateContainer(DockerClient client) {
        super(client, ContainerCreated.class);
    }

    @Override
    protected void decorate(SimpleRequest<Object, ContainerCreated> request) {
        super.decorate(request);

        request.post()
                .header("Content-Type", "application/json")
                .body(config)
                .pathBuilder("containers", "create")
                .query("name", name);
    }
}
