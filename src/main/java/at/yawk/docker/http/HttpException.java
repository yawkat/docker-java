package at.yawk.docker.http;

/**
 * @author yawkat
 */
public class HttpException extends RuntimeException {
    HttpException(String message) {
        super(message);
    }

    HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    HttpException(Throwable cause) {
        super(cause);
    }

    /**
     * Create a new exception of the same type that is caused by this exception.
     */
    HttpException rebuildHere() {
        return new HttpException(this);
    }

    static HttpException wrap(Throwable cause) {
        if (cause instanceof HttpException) {
            return ((HttpException) cause).rebuildHere();
        } else {
            return new HttpException(cause);
        }
    }
}
