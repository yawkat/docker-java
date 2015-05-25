package at.yawk.docker.model;

import java.time.Instant;
import lombok.Data;

/**
 * @author yawkat
 */
@Data
public class LogLine {
    /**
     * Time of this log entry or null if unknown.
     */
    private Instant time;
    private String line;
}
