package at.yawk.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Data;

/**
 * @author yawkat
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {
    @JsonProperty("Error")
    String error;
    @JsonProperty("ExitCode")
    Integer exitCode;
    @JsonProperty("FinishedAt")
    Instant finishedAt;
    @JsonProperty("OOMKilled")
    Boolean oomKilled;
    @JsonProperty("Paused")
    Boolean paused;
    @JsonProperty("Pid")
    Integer pid;
    @JsonProperty("Restarting")
    Boolean restarting;
    @JsonProperty("Running")
    Boolean running;
    @JsonProperty("StartedAt")
    Instant startedAt;
}
