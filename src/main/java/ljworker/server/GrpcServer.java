package ljworker.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

/**
 * gRPC server listening for client RPCs.
 */
public class GrpcServer {
    // TODO: these constants could be variables received from user args at runtime
    private static final String HOST = "localhost";
    private static final int PORT = 8443;

    public static void main(String[] args) throws IOException, InterruptedException {
        // Initialize gRPC server listening on HOST:PORT
        Server server = NettyServerBuilder.forAddress(new InetSocketAddress(HOST, PORT))
                // Add service handlers
                .addService(new LinuxJobServiceImpl())
                .addService(new AuthenticationServiceImpl())
                .build();

        System.out.println("Starting server...");
        server.start();
        System.out.println("Server started!");
        server.awaitTermination();
    }
}
