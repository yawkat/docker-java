package at.yawk.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Value;

/**
 * @author yawkat
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HostConfig {
    @JsonProperty("Binds")
    List<String> binds;
    @JsonProperty("Links")
    List<String> links;
    @JsonProperty("LxcConf")
    Map<String, String> lxcConf;
    @JsonProperty("PortBindings")
    Map<String, List<HostPort>> portBindings;
    /**
     * If set to true, all exposed ports (specified in the dockerfile) will be published on the host machine, though not
     * necessarily on the same port.
     */
    @JsonProperty("PublishAllPorts")
    Boolean publishAllPorts;
    @JsonProperty("Privileged")
    Boolean privileged;
    @JsonProperty("ReadonlyRootfs")
    Boolean readOnlyRootFs;
    @JsonProperty("Dns")
    List<String> dns;
    @JsonProperty("DnsSearch")
    List<String> dnsSearch;
    @JsonProperty("ExtraHosts")
    List<String> extraHosts;
    /**
     * List of containers to include volumes from.
     */
    @JsonProperty("VolumesFrom")
    List<String> volumesFrom;
    @JsonProperty("CapAdd")
    List<String> capAdd;
    @JsonProperty("CapDrop")
    List<String> capDrop;
    @JsonProperty("RestartPolicy")
    RestartPolicy restartPolicy;
    @JsonProperty("NetworkMode")
    String networkMode;
    @JsonProperty("Devices")
    List<String> devices;

    @JsonProperty("BlkioDeviceReadBps")
    List<DeviceRateLimit> blkioDeviceReadBps;
    @JsonProperty("BlkioDeviceWriteBps")
    List<DeviceRateLimit> blkioDeviceWriteBps;
    @JsonProperty("BlkioDeviceReadIOps")
    List<DeviceRateLimit> blkioDeviceReadIOps;
    @JsonProperty("BlkioDeviceWriteIOps")
    List<DeviceRateLimit> blkioDeviceWriteIOps;

    @Value
    public static class DeviceRateLimit {
        String path;
        int rate;
    }
}
