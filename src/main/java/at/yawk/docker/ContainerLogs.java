package at.yawk.docker;

import at.yawk.docker.http.DelimitedStreamDecoder;
import at.yawk.docker.http.StreamingRequest;
import at.yawk.docker.model.LogLine;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author yawkat
 */
@Accessors(fluent = true)
@Setter
public class ContainerLogs extends StreamQuery<Void, LogLine> {
    private String id;
    private Boolean stderr;
    private Boolean stdout;
    private Boolean stdin;
    private Boolean timestamps;
    private Boolean follow;
    private Integer tail;

    ContainerLogs(DockerClient client) {
        super(client);
    }

    @Override
    protected void decorate(StreamingRequest<Void, LogLine> request) {
        request.bodyDecoder(new DelimitedStreamDecoder<>(message -> {
            String s = message.toString(StandardCharsets.UTF_8);
            LogLine logLine = new LogLine();
            int start;
            if (timestamps != null && timestamps) {
                int separatorI = s.indexOf(' ');
                // trim []
                boolean inBrackets = s.charAt(0) == '[';
                String timestampString = inBrackets ?
                        s.substring(1, separatorI - 1) :
                        s.substring(0, separatorI);
                logLine.setTime(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(timestampString)));
                start = separatorI + 1;
            } else {
                start = 0;
            }
            // remove trailing \n and \r
            int end = s.length();
            while (end > start) {
                char c = s.charAt(end - 1);
                if (c == '\n' || c == '\r') {
                    end--;
                } else {
                    break;
                }
            }
            logLine.setLine(s.substring(start, end));
            return logLine;
        }));

        request.pathBuilder("containers", id, "logs")
                .query("stderr", AbstractQuery.flagToString(stderr))
                .query("stdout", AbstractQuery.flagToString(stdout))
                .query("stdin", AbstractQuery.flagToString(stdin))
                .query("timestamps", AbstractQuery.flagToString(timestamps))
                .query("follow", AbstractQuery.flagToString(follow))
                .query("tail", tail);
    }
}
