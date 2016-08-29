package at.yawk.docker.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author yawkat
 */
abstract class CompatSimpleChannelInboundHandler<I> extends SimpleChannelInboundHandler<I> {
    protected CompatSimpleChannelInboundHandler() {
    }

    protected CompatSimpleChannelInboundHandler(boolean autoRelease) {
        super(autoRelease);
    }

    protected CompatSimpleChannelInboundHandler(Class<? extends I> inboundMessageType) {
        super(inboundMessageType);
    }

    protected CompatSimpleChannelInboundHandler(Class<? extends I> inboundMessageType, boolean autoRelease) {
        super(inboundMessageType, autoRelease);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, I o) throws Exception {
        messageReceived(ctx, o);
    }

    // netty 5 compat

    protected abstract void messageReceived(ChannelHandlerContext ctx, I o) throws Exception;
}
