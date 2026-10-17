package com.framework.api;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

/** Local-only HTTP endpoint that records an incoming POST Content-Type header. */
final class OutboundRequestCaptureServer implements AutoCloseable {
    private final HttpServer server;
    private final AtomicReference<String> contentType = new AtomicReference<>();

    OutboundRequestCaptureServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(
                    InetAddress.getByName("127.0.0.1"), 0), 0);
            server.createContext("/posts", exchange -> {
                contentType.set(exchange.getRequestHeaders().getFirst("Content-Type"));
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
            });
            server.start();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot start local outbound-request capture endpoint", e);
        }
    }

    String baseUrl() {
        InetSocketAddress address = server.getAddress();
        try {
            return new URI("http", null, address.getAddress().getHostAddress(),
                    address.getPort(), null, null, null).toString();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot construct local capture endpoint URL", e);
        }
    }

    String contentType() {
        return contentType.get();
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
