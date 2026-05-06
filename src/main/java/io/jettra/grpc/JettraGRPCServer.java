package io.jettra.grpc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * Jettra-native gRPC server logic.
 * This class provides the HttpHandler needed to bridge JettraGRPC with any HttpServer (like JettraServer).
 */
public class JettraGRPCServer {
    private static final Logger logger = Logger.getLogger(JettraGRPCServer.class.getName());

    /**
     * Creates an HttpHandler for the given JettraService.
     * The consuming project should register this handler in its JettraServer instance.
     */
    public static HttpHandler createHandler(JettraService service) {
        return new NativeGRPCHandler(service);
    }

    private static class NativeGRPCHandler implements HttpHandler {
        private final JettraService service;

        public NativeGRPCHandler(JettraService service) {
            this.service = service;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            // Jettra-native gRPC headers
            exchange.getResponseHeaders().set("Content-Type", "application/grpc+jettra");
            exchange.sendResponseHeaders(200, 0);

            try (InputStream is = exchange.getRequestBody();
                 OutputStream os = exchange.getResponseBody()) {
                
                // Read Jettra-gRPC frame
                byte[] requestData = JettraProtocol.readFrame(is);
                
                logger.info("JettraGRPC: Handling native frame for " + service.getServiceName());
                
                // Dispatch logic would go here in a full implementation
                byte[] responseData = new byte[0]; 
                JettraProtocol.writeFrame(os, responseData);
            }
        }
    }
}
