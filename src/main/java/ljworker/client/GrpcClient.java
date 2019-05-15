package ljworker.client;

import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;


/**
 * gRPC client interface. This client interface allows a user to send
 * start/stop/status requests to a connected LinuxJobWorker.
 */
public class GrpcClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8443;

    private ManagedChannel channel;

    public GrpcClient() {}

    /** Initialize gRPC connection. */
    public void init() {
        // Initialize connection to ther server
        this.channel = NettyChannelBuilder.forAddress(HOST, PORT)
                // TODO: setup TLS/SSL encryption. RPCs sent as plain text for now.
                .usePlaintext()
                .build();
    }

    /** Once the channel is closed new incoming RPCs will be cancelled. */
    public void close() throws InterruptedException {
        channel.shutdown()
                .awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Send start request with provided args.
     * 
     * @param args Start RPC arguments
     */
    public void start(String[] args) {
        // TODO: start RPC
    }

    /**
     * Send stop request with provided args.
     * 
     * @param args Stop RPC arguments
     */
    public void stop(String[] args) {
        // TODO: stop RPC
    }

    /**
     * Send status request with provided args.
     * 
     * @param args Status RPC arguments
     */
    public void status(String[] args) {
        // TODO: status RPC
    }
}
