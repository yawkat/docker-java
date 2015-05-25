package at.yawk.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author yawkat
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerConfig {
    /**
     * Hostname of the container. Defaults to abbreviated ID.
     */
    @JsonProperty("Hostname")
    String hostname;
    @JsonProperty("Domainname")
    String domainName;
    @JsonProperty("User")
    String user;
    /**
     * Available memory in bytes.
     */
    @JsonProperty("Memory")
    Long memory;
    @JsonProperty("MemorySwap")
    Long memorySwap;
    @JsonProperty("CpuShares")
    Integer cpuShares;
    @JsonProperty("Cpuset")
    String cpuSet;
    /**
     * Whether we can send stdin to this container.
     */
    @JsonProperty("AttachStdin")
    Boolean attachStdin;
    /**
     * Whether we can read stdout from this container.
     */
    @JsonProperty("AttachStdout")
    Boolean attachStdout;
    /**
     * Whether we can read stderr from this container.
     */
    @JsonProperty("AttachStderr")
    Boolean attachStderr;
    /**
     * Whether this container will be run as TTY ('nicer' shell).
     */
    @JsonProperty("Tty")
    Boolean tty;
    @JsonProperty("OpenStdin")
    Boolean openStdin;
    @JsonProperty("StdinOnce")
    Boolean stdinOnce;
    /**
     * Env variables to pass through.
     */
    @JsonProperty("Env")
    List<String> env;
    @JsonProperty("Cmd")
    List<String> cmd;
    @JsonProperty("Entrypoint")
    String entrypoint;
    /**
     * Image to create this container from.
     */
    @JsonProperty("Image")
    String image;
    @JsonProperty("Volumes")
    Map<String, ObjectNode> volumes;
    @JsonProperty("WorkingDir")
    String workingDir;
    @JsonProperty("NetworkDisabled")
    Boolean networkDisabled;
    @JsonProperty("MacAddress")
    String macAddress;
    @JsonProperty("ExposedPorts")
    Map<String, Object> exposedPorts;
    @JsonProperty("SecurityOpts")
    List<String> securityOpts;
    /**
     * Host config to apply on this container.
     */
    @JsonProperty("HostConfig")
    HostConfig hostConfig;
}
