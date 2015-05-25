package at.yawk.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import lombok.Data;

/**
 * @author yawkat
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerListInfo {
    @JsonProperty("Id")
    String id;
    @JsonProperty("Image")
    String image;
    @JsonProperty("Command")
    String command;
    @JsonProperty("Created")
    Instant created;
    @JsonProperty("Status")
    String status;
    /**
     * Open ports of this container.
     */
    @JsonProperty("Ports")
    List<OpenPort> ports;
    @JsonProperty("SizeRw")
    Integer sizeRw;
    @JsonProperty("SizeRootFs")
    Integer sizeRootFs;
}
