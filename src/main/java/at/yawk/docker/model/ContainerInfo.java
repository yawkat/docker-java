package at.yawk.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author yawkat
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerInfo {
    @JsonProperty("AppArmorProfile")
    String appArmorProfile;
    @JsonProperty("Args")
    List<String> args;
    @JsonProperty("Config")
    ContainerConfig containerConfig;
    @JsonProperty("Created")
    Instant created;
    @JsonProperty("Driver")
    String driver;
    @JsonProperty("ExecDriver")
    String execDriver;
    // todo: what type is ExecIDs?
    // ExecIDs
    @JsonProperty("HostConfig")
    HostConfig hostConfig;
    @JsonProperty("HostnamePath")
    String hostnamePath;
    @JsonProperty("HostsPath")
    String hostsPath;
    @JsonProperty("Id")
    String id;
    @JsonProperty("Image")
    String image;
    @JsonProperty("MountLabel")
    String mountLabel;
    @JsonProperty("Name")
    String name;
    @JsonProperty("NetworkSettings")
    NetworkSettings networkSettings;
    @JsonProperty("Path")
    String path;
    @JsonProperty("ProcessLabel")
    String processLabel;
    @JsonProperty("ResolvConfPath")
    String resolvConfPath;
    @JsonProperty("RestartCount")
    Integer restartCount;
    @JsonProperty("State")
    State state;
    /**
     * All volumes mounted on this container. Keys are container paths, values are host paths.
     */
    @JsonProperty("Volumes")
    Map<String, String> volumes;
    /**
     * Keys are container paths, values whether they're writable.
     */
    @JsonProperty("VolumesRW")
    Map<String, Boolean> volumesRW;
}
