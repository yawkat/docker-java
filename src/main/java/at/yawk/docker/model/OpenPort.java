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
public class OpenPort {
    @JsonProperty("PrivatePort")
    Integer privatePort;
    @JsonProperty("PublicPort")
    Integer publicPort;
    @JsonProperty("Type")
    String type;
}
