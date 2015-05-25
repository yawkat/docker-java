package at.yawk.docker;

import at.yawk.docker.http.SimpleRequest;
import at.yawk.docker.model.ContainerListInfo;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import java.util.List;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author yawkat
 */
@Accessors(fluent = true)
@Setter
public class ListContainers extends JsonQuery<List<ContainerListInfo>> {
    private static final CollectionType TYPE = CollectionType.construct(
            List.class, SimpleType.construct(ContainerListInfo.class));

    private Boolean all;

    ListContainers(DockerClient client) {
        super(client, TYPE);
    }

    @Override
    protected void decorate(SimpleRequest<Object, List<ContainerListInfo>> request) {
        super.decorate(request);

        request.pathBuilder("containers", "json")
                .query("all", flagToString(all));
    }
}
