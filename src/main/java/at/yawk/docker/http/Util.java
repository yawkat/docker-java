package at.yawk.docker.http;

/**
 * @author yawkat
 */
class Util {
    private Util() {}

    public static void uninterruptible(InterruptibleRunnable runnable) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    runnable.run();
                    break;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static <T> T uninterruptible(InterruptibleSupplier<T> supplier) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    return supplier.get();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public interface InterruptibleRunnable {
        void run() throws InterruptedException;
    }

    public interface InterruptibleSupplier<T> {
        T get() throws InterruptedException;
    }
}
