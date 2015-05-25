package at.yawk.docker;

import at.yawk.docker.http.ResponsePromise;

/**
 * @author yawkat
 */
public interface Query<R> {
    ResponsePromise<R> send();
}
