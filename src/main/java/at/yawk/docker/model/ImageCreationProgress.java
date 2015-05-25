package at.yawk.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import java.io.IOException;
import lombok.Data;

/**
 * Generic message that will be returned by the image creation endpoint.
 *
 * @author yawkat
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageCreationProgress {
    @JsonDeserialize(using = LineDecoder.class)
    String stream;
    String progress;
    ProgressDetail progressDetail;
    String error;

    public boolean isError() {
        return getError() != null;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProgressDetail {
        int current;
        int total;
    }

    /**
     * String decoder that removes trailing newlines.
     */
    static class LineDecoder extends StdScalarDeserializer<String> {

        public LineDecoder() {
            super(String.class);
        }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            String text = p.getText();
            int end = text.length();
            while (end > 0) {
                char c = text.charAt(end - 1);
                if (c != '\n' && c != '\r') {
                    break;
                }
                end--;
            }
            return text.substring(0, end);
        }
    }
}
