package at.yawk.docker.http;

/**
 * @author yawkat
 */
interface ConnectionInitializer {
    void initialize(Connection connection);
}
