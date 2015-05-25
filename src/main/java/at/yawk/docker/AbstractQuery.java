package at.yawk.docker;

import at.yawk.docker.http.ResponsePromise;
import at.yawk.docker.http.SimpleRequest;

/**
 * @author yawkat
 */
abstract class AbstractQuery<R> implements Query<R> {
    final DockerClient client;

    AbstractQuery(DockerClient client) {
        this.client = client;
    }

    protected abstract void decorate(SimpleRequest<Object, R> request);

    @Override
    public ResponsePromise<R> send() {
        SimpleRequest<Object, R> req = SimpleRequest.<Object, R>create(client.httpClient);
        decorate(req);
        return req.send();
    }

    static String flagToString(Boolean b) {
        return b == null ? null :
                b ? "1" : "0";
    }
}
