package at.yawk.docker;

import at.yawk.docker.http.HttpClient;
import at.yawk.docker.http.JsonMessageEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Closeable;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author yawkat
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DockerClient implements Closeable {
    final HttpClient httpClient;

    final ObjectMapper objectMapper;
    final JsonMessageEncoder jsonEncoder;

    public ListContainers listContainers() {
        return new ListContainers(this);
    }

    public CreateContainer createContainer() {
        return new CreateContainer(this);
    }

    public StartContainer startContainer() {
        return new StartContainer(this);
    }

    public StopContainer stopContainer() {
        return new StopContainer(this);
    }

    public KillContainer killContainer() {
        return new KillContainer(this);
    }

    public DeleteContainer deleteContainer() {
        return new DeleteContainer(this);
    }

    public InspectContainer inspectContainer() {
        return new InspectContainer(this);
    }

    public ContainerLogs containerLogs() {
        return new ContainerLogs(this);
    }

    public CopyContainer copyContainer() {
        return new CopyContainer(this);
    }

    public AttachContainer attachContainer() {
        return new AttachContainer(this);
    }

    public BuildImage buildImage() {
        return new BuildImage(this);
    }

    public WaitContainer waitContainer() {
        return new WaitContainer(this);
    }

    @Override
    public void close() {
        httpClient.close();
    }

    ///// BUILDER

    public static Builder builder() {
        return new Builder();
    }

    @Accessors(fluent = true)
    @Setter
    public static class Builder {
        private URL url;
        private ObjectMapper objectMapper;

        private Builder() {}

        public Builder url(URL url) {
            this.url = url;
            return this;
        }

        public Builder url(String url) {
            try {
                return url(new URL(url));
            } catch (MalformedURLException e) {
                throw new UncheckedIOException(e);
            }
        }

        public DockerClient build() {
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
                objectMapper.findAndRegisterModules();
            }
            HttpClient httpClient = HttpClient.create(url);
            httpClient.setUrlRewrite(u -> "/v1.17" + u);
            return new DockerClient(
                    httpClient,
                    objectMapper,
                    JsonMessageEncoder.create(objectMapper)
            );
        }
    }
}
