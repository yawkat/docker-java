package at.yawk.docker;

import at.yawk.docker.http.ResponseStream;
import at.yawk.docker.http.StreamingRequest;
import at.yawk.docker.http.StreamingResponsePromise;

/**
 * @author yawkat
 */
abstract class StreamQuery<S, R> implements Query<ResponseStream<S, R>> {
    final DockerClient client;

    StreamQuery(DockerClient client) {
        this.client = client;
    }

    protected abstract void decorate(StreamingRequest<S, R> request);

    @Override
    public StreamingResponsePromise<S, R> send() {
        StreamingRequest<S, R> req = StreamingRequest.create(client.httpClient);
        decorate(req);
        return req.send();
    }
}
