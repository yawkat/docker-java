package at.yawk.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author yawkat
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkSettings {
    @JsonProperty("Bridge")
    String bridge;
    @JsonProperty("Gateway")
    String gateway;
    @JsonProperty("IPAddress")
    String ipAddress;
    @JsonProperty("IPPrefixLen")
    Integer ipPrefixLen;
    @JsonProperty("MacAddress")
    String macAddress;
    // todo: what type is PortMapping?
    /**
     * Ports that are exposed on the host machine. Key is the port spec string on the container (for example
     * "10000/tcp"), value are the ports on the host machine that expose the key.
     */
    @JsonProperty("Ports")
    Map<String, List<ExposedPort>> ports;
}
