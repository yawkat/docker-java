package at.yawk.docker.http;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.SneakyThrows;

/**
 * Promise that is returned once connection has been established by the Request class, and finishes once headers are
 * received (and body is received for non-streaming requests).
 *
 * @author yawkat
 */
public class ResponsePromise<R> extends CompletableFuture<R> implements StatusProvider {
    HttpResponse response = null;

    ResponsePromise() {}

    public String header(String key) {
        return response.headers().get(key);
    }

    @Override
    @SneakyThrows
    public HttpResponseStatus status() {
        return NettyCompat.status(response);
    }

    @Override
    public R get() throws InterruptedException, HttpException {
        try {
            return super.get();
        } catch (ExecutionException e) {
            throw HttpException.wrap(e.getCause());
        }
    }

    @Override
    public R get(long timeout, TimeUnit unit) throws InterruptedException, HttpException, TimeoutException {
        try {
            return super.get(timeout, unit);
        } catch (ExecutionException e) {
            throw HttpException.wrap(e.getCause());
        }
    }

    public R getUninterruptibly() throws HttpException {
        return Util.uninterruptible((Util.InterruptibleSupplier<R>) this::get);
    }
}
