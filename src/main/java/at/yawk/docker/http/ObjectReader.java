package at.yawk.docker.http;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import lombok.Getter;

/**
 * @author yawkat
 */
class ObjectReader<T> {
    private final Queue<T> objectQueue = new ArrayDeque<>();
    private final List<Throwable> exceptions = new ArrayList<>();
    @Getter private boolean closed = false;
    private ReadCallback<T> callback = null;

    synchronized void addException(Throwable exception) {
        exceptions.add(exception);
        if (callback != null) {
            callback.exceptionCaught(exception);
        }
        notifyAll();
    }

    synchronized void offer(T item) {
        if (callback == null) {
            objectQueue.offer(item);
            return;
        }
        callback.read(item);
    }

    synchronized void close() {
        closed = true;
        if (callback != null) {
            callback.close();
        }
        notifyAll();
    }

    public synchronized void setCallback(ReadCallback<T> callback) {
        T front;
        while ((front = objectQueue.poll()) != null) {
            callback.read(front);
        }
        exceptions.forEach(callback::exceptionCaught);
        if (closed) {
            callback.close();
            return;
        }
        this.callback = callback;
    }

    public synchronized void awaitClose() throws InterruptedException, HttpException {
        while (true) {
            if (!exceptions.isEmpty()) {
                HttpException e = HttpException.wrap(exceptions.get(0));
                // add others
                for (int i = 1; i < exceptions.size(); i++) {
                    e.addSuppressed(exceptions.get(i));
                }
                throw e;
            }
            if (closed) { break; }
            wait();
        }
    }
}
