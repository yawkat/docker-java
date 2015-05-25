package at.yawk.docker.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Request that supports streaming data to input and output.
 *
 * Note that if the endpoint uses standard HTTP (i.e. client sends headers, client sends body, server sends headers,
 * server sends body), the returned promise will only complete once the server has sent its headers, so you can't use
 * it
 * to write client data before that.
 *
 * @author yawkat
 */
@Accessors(fluent = true)
@Setter
public class StreamingRequest<S, R> extends AbstractHeaderRequest<ResponseStream<S, R>> implements ChannelWriter<S> {
    private static final Object SIGNAL_EOF = new Object();

    private MessageToByteEncoder<? super S> bodyEncoder;
    private ByteToMessageDecoder bodyDecoder;
    /**
     * Whether this request is chunked. Makes sense if the server side needs to know when the sent data ends.
     */
    private boolean chunked = false;

    final ObjectReader<R> reader = new ObjectReader<>();

    @Override
    protected ResponsePromise<ResponseStream<S, R>> createPromise() {
        return new StreamingResponsePromise<>(this);
    }

    private StreamingRequest(HttpClient client) {
        super(client);
    }

    @Override
    protected void handleLateException(Throwable e) {
        reader.addException(e);
        reader.close();
        connection.close();
    }

    @Override
    protected Connection acquireConnection() throws InterruptedException {
        // streaming requests can take time so their connections must not be pooled
        return client.acquireDetached();
    }

    @Override
    public StreamingRequest<S, R> get() {
        return (StreamingRequest<S, R>) super.get();
    }

    @Override
    public StreamingRequest<S, R> post() {
        return (StreamingRequest<S, R>) super.post();
    }

    @Override
    public StreamingRequest<S, R> delete() {
        return (StreamingRequest<S, R>) super.delete();
    }

    @Override
    public StreamingRequest<S, R> patch() {
        return (StreamingRequest<S, R>) super.patch();
    }

    @Override
    public StreamingRequest<S, R> header(String key, Object value) {
        return (StreamingRequest<S, R>) super.header(key, value);
    }

    @Override
    public StreamingRequest<S, R> path(String path) {
        return (StreamingRequest<S, R>) super.path(path);
    }

    public static <S, R> StreamingRequest<S, R> create(HttpClient client) {
        return new StreamingRequest<>(client);
    }

    public void write(S message) {
        connection.channel.write(message, connection.newWriterPromise());
    }

    public void flush() {
        connection.channel.flush();
    }

    public void writeAndFlush(S message) {
        connection.channel.writeAndFlush(message, connection.newWriterPromise());
    }

    /**
     * Mark this channels output as finished and notify the server that we have no more input.
     */
    public void finishWrite() {
        if (chunked) {
            connection.channel.writeAndFlush(new DefaultLastHttpContent(), connection.newWriterPromise());
        } else {
            flush();
        }
    }

    @Override
    public StreamingResponsePromise<S, R> send() {
        return (StreamingResponsePromise<S, R>) super.send();
    }

    @Override
    protected void init() {
        if (bodyDecoder != null) {
            connection.append(new MessageToMessageDecoder<HttpContent>() {
                @Override
                protected void decode(ChannelHandlerContext channelHandlerContext, HttpContent httpContent,
                                      List<Object> list)
                        throws Exception {
                    if (httpContent instanceof HttpResponse) {
                        // wrap response to deny access to content buffers and readd to pipeline
                        list.add(new DelegateHttpResponse((HttpResponse) httpContent));
                    }
                    ByteBuf content = httpContent.content();
                    if (content.isReadable()) {
                        content.retain();
                        list.add(content);
                    }
                    if (httpContent instanceof LastHttpContent) {
                        list.add(SIGNAL_EOF);
                    }
                }
            });
            connection.append(bodyDecoder);
            connection.append(new SimpleChannelInboundHandler<Object>(false) {
                @Override
                public boolean acceptInboundMessage(Object msg) throws Exception {
                    // http responses are handled below
                    return !(msg instanceof HttpResponse);
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void messageReceived(ChannelHandlerContext ctx, Object o) throws Exception {
                    if (o == SIGNAL_EOF) {
                        reader.close();
                        connection.close();
                        return;
                    }
                    reader.offer((R) o);
                }
            });
        }
        if (bodyEncoder != null) {
            connection.append(bodyEncoder);
        }
    }

    @Override
    protected void handleObject(HttpObject object) {
        super.handleObject(object);
        new MessageResponseDecoder<>(i -> new ResponseStream<>(StreamingRequest.this, connection))
                .decode(promise, null);
    }

    @Override
    public StreamingRequest<S, R> method(HttpMethod method) {
        return (StreamingRequest<S, R>) super.method(method);
    }

    @Override
    protected HttpRequest getRequest() {
        if (body == null) {
            // variable-sized body
            DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, buildPath());
            if (chunked) {
                request.headers().add("Transfer-Encoding", "chunked");
            }
            headers.forEach(request.headers()::add);
            return request;
        }
        return super.getRequest();
    }

    @RequiredArgsConstructor
    private static class DelegateHttpResponse implements HttpResponse {
        private final HttpResponse response;

        @Override
        public HttpResponseStatus status() {
            return response.status();
        }

        @Override
        public HttpResponse setStatus(HttpResponseStatus status) {
            return response.setStatus(status);
        }

        @Override
        public HttpResponse setProtocolVersion(HttpVersion version) {
            return response.setProtocolVersion(version);
        }

        @Override
        public HttpVersion protocolVersion() {
            return response.protocolVersion();
        }

        @Override
        public HttpHeaders headers() {
            return response.headers();
        }

        @Override
        public DecoderResult decoderResult() {
            return response.decoderResult();
        }

        @Override
        public void setDecoderResult(DecoderResult decoderResult) {
            response.setDecoderResult(decoderResult);
        }
    }
}
