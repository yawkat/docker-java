package at.yawk.docker.util;

import at.yawk.docker.http.ReadCallback;
import at.yawk.docker.http.ResponseStream;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import lombok.SneakyThrows;

/**
 * @author yawkat
 */
public class ResponseStreamIterator<T> implements Iterator<T> {
    private final Queue<T> queue = new ArrayDeque<>();
    private boolean closed;

    private ResponseStreamIterator() {}

    private synchronized void offer(T t) {
        queue.offer(t);
        notifyAll();
    }

    private synchronized void close() {
        closed = true;
        notifyAll();
    }

    @Override
    @SneakyThrows(InterruptedException.class)
    public synchronized boolean hasNext() {
        while (queue.isEmpty()) {
            if (closed) { return false; }
            wait();
        }
        return true;
    }

    @Override
    public synchronized T next() {
        if (!hasNext()) { throw new NoSuchElementException(); }
        // assume we have an item
        return queue.poll();
    }

    // factory

    public static <T> ResponseStreamIterator<T> attach(ResponseStream<?, T> stream) {
        ResponseStreamIterator<T> iterator = new ResponseStreamIterator<>();
        stream.readCallback(new ReadCallback<T>() {
            @Override
            public void read(T item) {
                iterator.offer(item);
            }

            @Override
            public void close() {
                iterator.close();
            }
        });
        return iterator;
    }
}
