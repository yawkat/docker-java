package at.yawk.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author yawkat
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExposedPort {
    /**
     * Host IP in a docker swarm or sometimes 0.0.0.0 in standalone.
     */
    @JsonProperty("HostIp")
    String hostIp;
    /**
     * Port on the host.
     */
    // HostPort is actually returned as a string by docker but luckily jackson will parse that just fine.
    @JsonProperty("HostPort")
    Integer hostPort;
}
