package at.yawk.docker.model;

import lombok.Data;

/**
 * @author yawkat
 */
@Data
public class AttachLine {
    public static final byte STDIN = 0;
    public static final byte STDERR = 1;
    public static final byte STDOUT = 2;
    public static final byte TTY = -1;

    /**
     * The stream this line belongs to or {@link TTY} if this is a tty container (which doesn't differentiate between
     * streams).
     */
    private byte stream;
    /**
     * The actual text of this line, without trailing newline.
     */
    private String line;
}
