package ljworker.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

/** gRPC server listening for client RPCs. */
public class GrpcServer {
    private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());
    private static final String HOST = "localhost";
    private static final int PORT = 8443;

    private Server server;

    /**
     * Initialize & start gRPC server
     * 
     * @throws IOException
     * @throws SSLException
     */
    public void start() throws SSLException, IOException {
        server = NettyServerBuilder.forAddress(new InetSocketAddress(HOST, PORT))
                .addService(new LinuxJobServiceImpl()) // add LinuxJobService
                .build()
                .start();
        logger.info("Server started, listening on " + PORT);
        Runtime.getRuntime()
                // registered shutdown hook will be run when the JVM begins shutdown
                .addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        // Use stderr here since the logger may have been reset by its JVM shutdown
                        // hook.
                        System.err.println("*** shutting down gRPC server since JVM is shutting down");
                        GrpcServer.this.stop();
                        System.err.println("*** server shut down");
                    }
                });
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon
     * threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Initiates shutdown. Preexisting calls will continue, but new calls will be
     * rejected.
     */
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
}
