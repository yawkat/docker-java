package at.yawk.docker.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.util.AttributeKey;
import java.io.Closeable;
import java.net.URL;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yawkat
 */
@Slf4j
public class HttpClient implements Closeable {
    private static final AttributeKey<Connection> CONNECTION_ATTRIBUTE = AttributeKey.newInstance("connection");

    private static final int MAX_CHANNEL_COUNT = 4;

    private final Bootstrap bootstrap;
    final ChannelPool channelPool;
    final String host;

    @Setter UrlRewrite urlRewrite = UrlRewrite.IDENTITY;

    private HttpClient(Bootstrap bootstrap, String host) {
        this.bootstrap = bootstrap;
        this.host = host;

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                initConnection(ch);
            }
        });
        this.channelPool = new FixedChannelPool(bootstrap, new AbstractChannelPoolHandler() {
            @Override
            public void channelCreated(Channel ch) throws Exception {
                initConnection(ch);
            }
        }, MAX_CHANNEL_COUNT);
    }

    public static HttpClient create(URL url) {
        String file = url.getFile();
        if (!file.isEmpty() && !file.equals("/")) {
            throw new IllegalArgumentException("URL path not supported");
        }

        String host = url.getHost();
        int port = url.getPort();
        if (port == -1) {
            switch (url.getProtocol()) {
            case "http":
                port = 80;
                break;
            case "https":
                port = 443;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported protocol " + url.getProtocol());
            }
        }

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port);
        return create(bootstrap, host);
    }

    public static HttpClient create(Bootstrap bootstrap, String host) {
        return new HttpClient(bootstrap, host);
    }

    Connection acquireDetached() throws InterruptedException {
        ChannelFuture future = bootstrap.connect();
        future.sync();
        Connection connection = getConnection(future.channel());
        connection.attached = false;
        return connection;
    }

    Connection acquireAttached() throws InterruptedException {
        Channel channel = channelPool.acquire().sync().getNow();
        Connection connection = getConnection(channel);
        connection.attached = true;
        return connection;
    }

    private static Connection getConnection(Channel channel) {
        return channel.attr(CONNECTION_ATTRIBUTE).get();
    }

    @Override
    public void close() {
        bootstrap.group().shutdownGracefully();
    }

    private void initConnection(Channel ch) {
        Connection connection = new Connection(HttpClient.this, ch);
        ch.pipeline().addLast(new ChannelHandlerAdapter() {
            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                connection.handleException(cause);
            }
        });
        ch.pipeline().addLast(new HttpClientCodec());
        connection.lastHttpHandler = ch.pipeline().last();
        ch.attr(CONNECTION_ATTRIBUTE).set(connection);
    }
}
