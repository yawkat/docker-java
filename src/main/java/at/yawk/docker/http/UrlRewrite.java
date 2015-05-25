package at.yawk.docker.http;

/**
 * Interface used for rewriting path names in a http client.
 *
 * @author yawkat
 */
public interface UrlRewrite {
    UrlRewrite IDENTITY = url -> url;

    String rewrite(String url);
}
