package at.yawk.docker.http;

import java.net.URLEncoder;
import lombok.SneakyThrows;

/**
 * @author yawkat
 */
public final class PathBuilder {
    private final StringBuilder path = new StringBuilder();
    private final StringBuilder query = new StringBuilder();

    public PathBuilder append(String item) {
        if (item == null) {
            throw new NullPointerException("item");
        }
        if (item.charAt(0) != '/') { path.append('/'); }
        if (item.charAt(item.length() - 1) == '/') {
            path.append(item, 0, item.length() - 1);
        } else {
            path.append(item);
        }
        return this;
    }

    public PathBuilder append(String... items) {
        for (String item : items) {
            append(item);
        }
        return this;
    }

    @SneakyThrows
    public PathBuilder query(String key, Object value) {
        if (value != null) {
            if (query.length() == 0) {
                query.append('?');
            } else {
                query.append('&');
            }
            query.append(key).append('=').append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return this;
    }

    String complete() {
        path.append(query);
        // make sure subsequent calls don't do weird things
        query.setLength(0);
        return path.toString();
    }

    @Override
    public String toString() {
        return path.toString() + query;
    }
}
